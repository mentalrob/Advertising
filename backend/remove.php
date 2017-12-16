<?php
include 'ayar.php';
if(isset($_GET['key'])){
	if($_GET['key'] == $Key){
		$dosya = fopen("credits.txt", "w");
		fwrite($dosya, "");
		fclose($dosya);
	}
}
?>