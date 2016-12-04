<?php
  $input=file_get_contents('php://input');
  $parts=explode(":",$input);

  $filename="/home/2015/fglozm/public_html/" . $parts[0];
  $fileData=$parts[1];
  
  if(strpos($filename, '.txt')) {
    fopen($filename,"x");
    file_put_contents($filename, $fileData, LOCK_EX);
  }
?>