<?php 
// add new user php file

session_start();
ob_start();
ob_end_flush();	

$con = new mysqli('127.0.0.1','root','catsavageslammin69','PartyPal');
if ($con->errno) {
	die('Error: ' . mysqli_error($con));		
}

$query_one = "SELECT userlist.phonenumber,userlist.fname,userlist.lastgps,(now() - userlist.lastcheckintime)/196 AS TimeSinceCheckIn,grouplist.groupphrase,userlist.notified FROM userlist INNER JOIN grouplist ON userlist.phonenumber=grouplist.phonenumber WHERE userlist.lastcheckintime < (now() - interval 75 minute)";
$query_two = "SELECT grouplist.phonenumber FROM grouplist WHERE phonenumber <> ? AND groupphrase = ?";

$stmt = $con->stmt_init();
$stmt->prepare($query_one);
if ($stmt) {
	$stack_one = array();
	$row_one = array();
	$stmt->execute();
	$stmt->bind_result($phone,$name,$lgps,$timer,$gphrase,$notified);
	while ($stmt->fetch()) {
		$row_one['phonenumber'] = $phone;
		$row_one['fname'] = $name;
		$row_one['lastgps'] = $lgps;
		$row_one['TimeSinceCheckIn'] = $timer;
		$row_one['groupphrase'] = $gphrase;
		$row_one['notified'] = $notified;
		if ($notified == 'no') {
			array_push($stack_one,$row_one);
		}
	}
	$stmt->close();
}

for ($i=0;$i<count($stack_one);$i++) {
	$stmt = $con->stmt_init();
	$stmt->prepare($query_two);
	if ($stmt) {
		$stack_two = array();
		$stmt->bind_param("ss",$stack_one[$i]['phonenumber'],$stack_one[$i]['groupphrase']);
		$stmt->execute();
		$stmt->bind_result($phone);
		while ($stmt->fetch()) {
			array_push($stack_two,$phone);
		}
		$stmt->close();
	}
	for($j=0;$j<count($stack_two);$j++) {
		shell_exec("curl -X POST 'https://api.twilio.com/2010-04-01/Accounts/ACe2175b27304e434936aecb0a0df846ac/SMS/Messages.json' -d 'From=9199487191' -d 'To=".$stack_two[$j]."' -d 'Body=PartyPal%3A+".$stack_one[$i]['fname']."+(".$stack_one[$i]['phonenumber'].")+has+not+checked-in+in+".floor($stack_one[$i]['TimeSinceCheckIn'])."+minutes.+Last+location+".$stack_one[$i]['lastgps']."' -u ACe2175b27304e434936aecb0a0df846ac:540122caef6df665022c6ff467d36467");
		$stmt = $con->stmt_init();
		$stmt->prepare("UPDATE userlist SET notified='yes' WHERE phonenumber = ?");
		if ($stmt) {
			$stmt->bind_param("s",$stack_one[$i]['phonenumber']);
			$stmt->execute();
			$stmt->close();
		}
	}
}



$obj = json_decode($_POST['json'],true);
$output = array();

switch ($obj['action']) {
       case 'add_user':
			$fname = $obj['fname'];
			$phonenumber = $obj['phonenumber'];
			$gps = $obj['gps'];
			$phonenumber = preg_replace("/[^0-9]/", "", $phonenumber);

			$stmt = $con->stmt_init();
			$stmt->prepare("INSERT INTO userlist (fname,phonenumber,lastgps) VALUES (?,?,?)");
			if ($stmt) {
				$stmt->bind_param("sss",$fname,$phonenumber,$gps);
				$stmt->execute();
				$stmt->close();
				
				$output['action'] = "added_user";
				$output['name'] = $fname;
				$output['number'] = $phonenumber;
				$output['location'] = $gps;
				
				echo json_encode($output);
			}
			exit();
		case 'check_in':
			$phonenumber = $obj['phonenumber'];
			$gps = $obj['gps'];
			$phonenumber = preg_replace("/[^0-9]/", "", $phonenumber);
			$phonenumber = substr($phonenumber, -10);
			
			$stmt = $con->stmt_init();
			$stmt->prepare("UPDATE userlist SET lastgps = ?, lastcheckintime = NOW() WHERE phonenumber = ?");
			if ($stmt) {
				$stmt->bind_param("ss",$gps,$phonenumber);
				$stmt->execute();
				$stmt->close();
				
				$output['action'] = 'updated_user';
				$output['number'] = $phonenumber;
				$output['location'] = $gps;
				
				echo json_encode($output);
			}
			
			shell_exec("echo curl -X POST 'https://api.twilio.com/2010-04-01/Accounts/ACe2175b27304e434936aecb0a0df846ac/SMS/Messages.json' -d 'From=9199487191' -d 'To=".$phonenumber."' -d 'Body=PartyPal%3A+Please+check+in+now' -u ACe2175b27304e434936aecb0a0df846ac:540122caef6df665022c6ff467d36467 | at now + 1 hour");
			exit();
		case 'add_groupmember':
			$phrase = $obj['groupphrase'];
			for($i=0; $i<count($obj['phonenumbers']); $i++) {
				$phonenumber = $obj['phonenumbers'][$i];
				$phonenumber = preg_replace("/[^0-9]/", "", $phonenumber);
				$phonenumber = substr($phonenumber, -10);

				$stmt = $con->stmt_init();
				$stmt->prepare("INSERT INTO grouplist (groupphrase,phonenumber) VALUES (?,?)");
				if ($stmt) {
					$stmt->bind_param("ss",$phrase,$phonenumber);
					$stmt->execute();
					$stmt->close();
					
					$output['action'] = "added_to_group";
					$output['number'] = $phonenumber;
					$output['group'] = $phrase;
				
					echo json_encode($output);
				}
			}
			exit();
		case 'remove_groupmember':
			$phrase = $obj['groupphrase'];
			for($i=0; $i<count($obj['phonenumbers']); $i++) {
				$phonenumber = $obj['phonenumbers'][$i];
				$phonenumber = preg_replace("/[^0-9]/", "", $phonenumber);
				$phonenumber = substr($phonenumber, -10);

				if ($phrase == "*") { // if there is no phrase, remove from all groups (used for checkout for the night)
					$stmt = $con->stmt_init();
					$stmt->prepare("DELETE FROM grouplist WHERE phonenumber = ?");
					if ($stmt) {
						$stmt->bind_param("s",$phonenumber);
						$stmt->execute();
						$stmt->close();
						
						$output['action'] = "checked_out";
						$output['number'] = $phonenumber;
						
						echo json_encode($output);
					}
				} else { // other wise remove from specified group phrase
					$stmt = $con->stmt_init();
					$stmt->prepare("DELETE FROM grouplist WHERE phonenumber = ? AND groupphrase = ?");
					if ($stmt) {
						$stmt->bind_param("ss",$phonenumber,$phrase);
						$stmt->execute();
						$stmt->close();
						
						$output['action'] = "removed_from_group";
						$output['number'] = $phonenumber;
						$output['group'] = $phrase;
						
						echo json_encode($output);
					}
					
				}

			}
			exit();
		case 'get_group_status':
			$phrase = $obj['groupphrase'];

			$stmt = $con->stmt_init();
			$stmt->prepare("SELECT grouplist.phonenumber, userlist.fname, userlist.lastgps, ((now() - userlist.lastcheckintime)/196) AS TimeSinceCheckIn FROM userlist RIGHT JOIN grouplist ON userlist.phonenumber=grouplist.phonenumber WHERE groupphrase = ?");
			if ($stmt) {
				$stmt->bind_param("s",$phrase);
				$stack = array();
				$row = array();
				$stmt->execute();
				$stmt->bind_result($phonenumber,$fname,$gps,$timer);
				while ($stmt->fetch()) {
					$phonenumber = preg_replace("/[^0-9]/", "", $phonenumber);
					$phonenumber = substr($phonenumber, -10);
					$row['phonenumber'] = $phonenumber;
					$row['fname'] = $fname;
					$row['lastgps'] = $gps;
					$row['TimeSinceCheckIn'] = $timer;
					array_push($stack,$row);
				}
				$stmt->close();
				
				echo json_encode($stack);
			}
			exit();
		case 'get_group':
			$phonenumber = $obj['phonenumber'];
			$phonenumber = preg_replace("/[^0-9]/", "", $phonenumber);
			$phonenumber = substr($phonenumber, -10);
			
			$stmt = $con->stmt_init();
			$stmt->prepare("SELECT grouplist.groupphrase FROM grouplist WHERE phonenumber = ?");
			if ($stmt) {
				$stmt->bind_param("s",$phonenumber);
				$stack = array();
				$stmt->execute();
				$stmt->bind_result($phrase);
				while ($stmt->fetch()) {
					array_push($stack,$phrase);
				}
				$stmt->close();
				
				echo json_encode($stack);
			}
			exit();
}


?>