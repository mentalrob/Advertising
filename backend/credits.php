<?php
include 'ayar.php';
if(isset($_GET['key'])){
	if($_GET['key'] == $Key){
		$dosya = fopen("credits.txt", "a+");
		if(filesize('credits.txt') > 0){
			$icerik = fread($dosya, filesize('credits.txt'));
			echo $icerik;
		}
		fclose($dosya);
	}
}
?>