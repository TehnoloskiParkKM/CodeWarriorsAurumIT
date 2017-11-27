<?php 

//getting the database conection
require_once 'dbconnect.php';

//an array to display response
$response = array();

//if it is an api call?
if(isset($_GET['apicall'])){

	switch($_GET['apicall']){

		case 'registration':

 		//checking the parameters required are available or not 
		if(isTheseParametersAvailable(array('kod','androidID'))){

 			//getting values 
			$kod = md5($_POST['kod']);
			$androidID = $_POST['androidID'];
			$androidIDDB = $_POST['androidID']; 
			
			//checking if verification code exist in database
			$stmt = $con->prepare("SELECT androidID FROM verifikacijakorisnika WHERE kod = ?");
			$stmt->bind_param("s", $kod);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($androidID);
			$stmt->fetch();

 			//if the verification code already exist and there is android ID paired with it 
			if($stmt->num_rows > 0){
				if($androidID==0){
					//if user is new and code is not used 
					$stmt = $con->prepare("UPDATE verifikacijakorisnika SET androidID = ? WHERE kod = ?");
					$stmt->bind_param("ss", $androidIDDB, $kod);

 					//if android ID is successfully added to the database 
					if($stmt->execute()){ 
						echo '200 OK'; 
					}					
				}
				else{
					//if there is already android ID paired with that ver. code
					echo 'Code already used! 401 UNAUTHORISED';
					$stmt->close();
				}
			}
			//ver. code is not in the database
			else{
				echo 'Code not found! 401 UNAUTHORISED';
				$stmt->close();
			}
		}
		else{
			echo 'BAD PARAMETERS! 401 UNAUTHORISED'; 
		}

		break; 

		case 'validation':

 		//for checking if device is registered we need the verification code and android ID 
		if(isTheseParametersAvailable(array('kod', 'androidID'))){
 			//getting values 
			$kod = md5($_POST['kod']); 
			$androidID = $_POST['androidID'];

 			//creating the query 
			$stmt = $con->prepare("SELECT androidID FROM verifikacijakorisnika WHERE kod = ? AND androidID = ?");
			$stmt->bind_param("ss",$kod, $androidID);

			$stmt->execute();

			$stmt->store_result();

 			//if the devise exist with given pair 
			if($stmt->num_rows > 0){
				$response = 'VALIDATION SUCCESSFULL - 200 OK'; 
			}
			else{
 				//if the user not found 
				echo 'VALIDATION UNSUCCESSFULL - 401 UNAUTHORISED';
			}
		}
		else{
			echo 'BAD PARAMETERS! 401 UNAUTHORISED'; 
		}

		break; 

		default: 
		echo 'Invalid Operation Called - 401 UNAUTHORISED';
	}

}
else{
 //if it is not api call 
	echo 'Invalid API Call';
}

 //displaying the response in json structure 
echo json_encode($response);

//function validating all the paramters are available 
function isTheseParametersAvailable($params){

 //traversing through all the parameters 
	foreach($params as $param){
 //if the paramter is not available
		if(!isset($_POST[$param])){
 //return false 
			return false; 
		}
	}
 //return true if every param is available 
	return true; 
}
?>
