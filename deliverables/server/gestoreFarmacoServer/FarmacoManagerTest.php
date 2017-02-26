<?php

require_once "FarmacoManager.php";

date_default_timezone_set("UTC");

// Active assert and make it quiet
assert_options(ASSERT_ACTIVE, true);
assert_options(ASSERT_WARNING, true);
assert_options(ASSERT_QUIET_EVAL, false);

// Set up the callback
assert_options (ASSERT_CALLBACK, 'assert_failed');


function assert_failed($file, $line, $expr) {
    print "Assertion failed in $file on line $line: $expr\n";
}

Class FarmacoManagerTest{

  private $account;
  private $access;
  private $directoryServer = "mobilfarm.cqplmnbzeyqt.us-east-1.rds.amazonaws.com:3306";
  private $username = "mobilFarm";
  private $password = "mobilFarm";
  private $dbName = "mobilFarm";

  public function __construct()
  {
    $this->account = new FarmacoManager();

    /* Connetto al Database Server */
    $this->access = mysql_connect($this->directoryServer, $this->username, $this->password) or die(mysql_error());

    /* Seleziono il Database */
    mysql_select_db($this->dbName);
  }

  public function testRicercaFarmaco()
  {
      echo "\nricercaFarmaco(\"Airtal\")\n";
      $result = json_decode($this->account->ricercaFarmaco("Airtal"), TRUE);

      assert($result['data']['count'] == 0);
      echo "Test completato!\n";
  }


  public function testCreaFarmaco()
  {
      echo "\ncreaFarmaco(\"Airtal\", \"10\")\n";
      $result1 = json_decode($this->account->creaFarmaco("Airtal", "10"), TRUE);

      $query = 'SELECT IDFarmaco FROM Farmaci WHERE Nome=\'Airtal\' AND Composizione =\'10\'';
      $result2 = mysql_query($query) or die(mysql_error());

      while ($record=mysql_fetch_assoc($result2)) $id = $record['IDFarmaco'];
      assert($result1['data'] == $id);

      mysql_query("DELETE FROM Farmaci WHERE IDFarmaco=\"".$id."\"") or die(mysql_error());

      echo "Test completato!\n";
  }

  public function testEsistenzaFarmaco()
  {
      echo "\nesistenzaFarmaco(\"Targin\", \"10\")\n";
      $result = json_decode($this->account->esistenzaFarmaco("Targin", "10"), TRUE);

      assert($result['data']['count'] != 0);
      echo "Test completato!\n";
  }
}

$test = new FarmacoManagerTest();
$test->testRicercaFarmaco();
$test->testCreaFarmaco();
$test->testEsistenzaFarmaco();
?>
