<?php
$sql=mysql_connect("localhost","minecraft","mmmmmm");
mysql_select_db("minecraft");
$q="SELECT `timestamp` from uptime where id=1";
$r=mysql_query($q);
$rr=mysql_fetch_row($r);
$timeoffirstrun=$rr[0];
$q="SELECT UNIX_TIMESTAMP() FROM uptime";
$r=mysql_query($q);
$rr=mysql_fetch_row($r);
$timenow=$rr[0];
$q="SELECT SUM(`timestamp`) from uptime WHERE event='StartEvent'";
$r=mysql_query($q);
$rr=mysql_fetch_row($r);
$timeofruns=$rr[0];
$q="SELECT SUM(`timestamp`) from uptime WHERE event='StopEvent'";
$r=mysql_query($q);
$rr=mysql_fetch_row($r);
$timeofoffs=$rr[0];
$q="SELECT SUM(`timetaken`) from uptime WHERE event='CrashEvent'";
$r=mysql_query($q);
$rr=mysql_fetch_row($r);
$timeofcrashes=$rr[0];
$q="SELECT event from uptime order by id DESC LIMIT 1";
$r=mysql_query($q);
$rr=mysql_fetch_row($r);
$latestevent=$rr[0];

$totaltimerunning=$timeofoffs-$timeofruns-$timeofcrashes;
$totaltimefromfirstrun=$timenow-$timeoffirstrun;
if($latestevent!="StartEvent")
{
  if($totaltimerunning>0){
  $Uptime=number_format(($totaltimerunning/$totaltimefromfirstrun)*100,2,".",",")."%";
  }
  else
  {
  $Uptime="0.00%";
  }
}
else
{
$q="SELECT `timestamp` from uptime order by id DESC LIMIT 1";
$r=mysql_query($q);
$rr=mysql_fetch_row($r);
$runtime=$rr[0];
$totaltimerunning=$totaltimerunning+$runtime+($timenow-$runtime);
  if($totaltimerunning>0){
  $Uptime=number_format(($totaltimerunning/$totaltimefromfirstrun)*100,2,".",",")."%";
  }
  else
  {
  $Uptime="0.00%";
  }
//$Uptime="Server is now running, uptime may be inaccurate (".number_format(($totaltimerunning/$totaltimefromfirstrun)*100,2,".",",")."%)";
}
//echo $timeoffirstrun."<BR/>";
//echo $timenow."<BR/>";
//echo $timeofruns."<BR/>";
//echo $timeofoffs."<BR/>";
//echo $timeofcrashes."<BR/>";
//echo $totaltimerunning."<BR/>";
//echo $totaltimefromfirstrun."<BR/>";
echo $Uptime;//."<BR/>";
?>