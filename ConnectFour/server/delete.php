<?php
  $file=file_get_contents('php://input');
  if(strpos($file, '.txt')) {
    $base_directory = "/home/2015/fglozm/public_html/";
    unlink($base_directory . $file);
  }
?>