<?php //script.php?u=AES(Sahip)&url=(AES)url&cost=AES(Cost)
include 'AES.php';
include 'ayar.php';
function GetIP(){
	return $_SERVER['REMOTE_ADDR'];
}
function strToHex($string){
	$hex = '';
	for ($i=0; $i<strlen($string); $i++){
		$ord = ord($string[$i]);
		$hexCode = dechex($ord);
		$hex .= substr('0'.$hexCode, -2);
	}
	return strToUpper($hex);
}
function hexToStr($hex){
	$string='';
	for ($i=0; $i < strlen($hex)-1; $i+=2){
		$string .= chr(hexdec($hex[$i].$hex[$i+1]));
	}
	return $string;
}
$object = new stdClass();
if(isset($_GET['u']) && isset($_GET['url']) && isset($_GET['cost'])){
	$object->u = $_GET['u'];
	$object->url = $_GET['url'];
	$object->cost = $_GET['cost'];
}else{
	$object->u = "";
	$object->url = "";
	$object->cost = "";
	Echo "error";
	exit();
}

$yonlendirme = new stdClass();
$AES = new AES(null,$Key,128,"cbc",$IV); //$data = null, $key = null, $blockSize = null, $mode = null,$IV = ""
$yonlendirme->u = $AES->setData($object->u)->decrypt();
$yonlendirme->u = hexToStr(str_replace("0E", "", strToHex($yonlendirme->u)));
$yonlendirme->url = $AES->setData($object->url)->decrypt();
$yonlendirme->url = hexToStr(str_replace("0E", "", strToHex($yonlendirme->url)));
$yonlendirme->cost = $AES->setData($object->cost)->decrypt();
$yonlendirme->cost = hexToStr(str_replace("0E", "", strToHex($yonlendirme->cost)));
$yonlendirme->ip = GetIP();
$yonlendirme->ip = hexToStr(str_replace("0E", "", strToHex($yonlendirme->ip)));
$dosya = fopen("credits.txt", "a+");
if(filesize('credits.txt') > 0){
	fwrite($dosya, "," . $yonlendirme->u . ":" . $yonlendirme->ip . ":" . $yonlendirme->cost);
}else{
	fwrite($dosya, $yonlendirme->u . ":" . $yonlendirme->ip . ":" . $yonlendirme->cost);
}
fclose($dosya);
echo "<meta http-equiv=\"refresh\" content=\"0;URL=". $yonlendirme->url . "\">";
?>