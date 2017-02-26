<?php

require_once "AccountManager.php";

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

Class AccountManagerTest{

  private $account;
  private $access;
  private $directoryServer = "mobilfarm.cqplmnbzeyqt.us-east-1.rds.amazonaws.com:3306";
  private $username = "mobilFarm";
  private $password = "mobilFarm";
  private $dbName = "mobilFarm";

  public function __construct()
  {
    $this->account = new AccountManager();

    /* Connetto al Database Server */
    $this->access = mysql_connect($this->directoryServer, $this->username, $this->password) or die(mysql_error());

    /* Seleziono il Database */
    mysql_select_db($this->dbName);
  }

  public function testLogin()
  {
      echo "\nlogin(\"alb\", \"vol\")\n";
      $result = json_decode($this->account->login("alb", "vol"), TRUE);
      assert(!$result['data']['credenziali_corrette']);
      echo "Test completato!\n";
  }

  public function testControlloCredenziali()
  {
      echo "\ncontrolloCredenziali(\"volpea.93@gmail.com\")\n";
      $result = $this->account->controlloCredenziali("volpea.93@gmail.com");
      assert($result == 1);
      echo "Test completato!\n";
  }

  public function testRegistraAccountPaziente()
  {
      echo "\n".'registraAccountPaziente("pincopalla@gmail.com", "password", "Pinco", "Palla", "M", "Paziente", "JEWDJF23", "353232", "", "Salerno", "03/12/1993", "Salerno", "Salerno")'."\n";
      $this->account->registraAccountPaziente("pincopalla@gmail.com", "password", "Pinco", "Palla", "M", "Paziente", "JEWDJF23", "353232", "", "Salerno", "03/12/1993", "Salerno", "Salerno");

      $query = 'SELECT Mail, Nome, Cognome, Sesso, CodiceFiscale, Cellulare, CittaResidenza, DettagliClinici, Residenza, DataNascita, LuogoNascita, NumeroSOS1, NumeroSOS2
                FROM Utenti, Pazienti WHERE Utenti.UserID=Pazienti.UserID AND Utenti.UserID = \''.md5("pincopalla@gmail.com").'\'';

      $result = mysql_query($query) or die(mysql_error());
      $record = mysql_fetch_array($result);

      assert('pincopalla@gmail.com' == $record['Mail']);
      assert('Pinco' == $record['Nome']);
      assert('Palla' == $record['Cognome']);
      assert('M' == $record['Sesso']);
      assert('JEWDJF23' == $record['CodiceFiscale']);
      assert('353232' == $record['Cellulare']);
      assert('' == $record['DettagliClinici']);
      assert('Salerno' == $record['CittaResidenza']);
      assert('Salerno' == $record['Residenza']);
      assert('03/12/1993' == $record['DataNascita']);
      assert('Salerno' == $record['LuogoNascita']);

      mysql_query("DELETE FROM Pazienti WHERE UserID='".md5("pincopalla@gmail.com")."'") or die(mysql_error());
      mysql_query("DELETE FROM Utenti WHERE UserID='".md5("pincopalla@gmail.com")."'") or die(mysql_error());

			//Chiudo la connessione al DB
			mysql_close($access);
      echo "Test completato!\n";
  }

  public function testRegistraAccountDottore()
  {
      echo "\n".'registraAccountDottore("lucarossi@gmail.com", "password", "Luca", "Rossi", "M", "Dottore", "24324324", "Salerno","SA", "Medico di base", "Salerno", "353232")'."\n";
      $this->account->registraAccountDottore("lucarossi@gmail.com", "password", "Luca", "Rossi", "M", "Dottore", "24324324", "Salerno", "SA", "Medico di base", "Salerno", "353232");

      $query = 'SELECT Mail, Nome, Cognome, Sesso, IDAlbo, Citta, Provincia, Specializzazione, IndirizzoStudio, TelefonoStudio
                FROM Utenti, Dottori WHERE Utenti.UserID=Dottori.UserID AND Utenti.UserID = \''.md5("lucarossi@gmail.com").'\'';

      $result = mysql_query($query) or die(mysql_error());
      $record = mysql_fetch_array($result);

      assert('lucarossi@gmail.com' == $record['Mail']);
      assert('Luca' == $record['Nome']);
      assert('Rossi' == $record['Cognome']);
      assert('M' == $record['Sesso']);
      assert('24324324' == $record['IDAlbo']);
      assert('Salerno' == $record['Citta']);
      assert('SA' == $record['Provincia']);
      assert('Medico di base' == $record['Specializzazione']);
      assert('Salerno' == $record['IndirizzoStudio']);
      assert('353232' == $record['TelefonoStudio']);

      mysql_query("DELETE FROM Dottori WHERE UserID='".md5("lucarossi@gmail.com")."'") or die(mysql_error());
      mysql_query("DELETE FROM Utenti WHERE UserID='".md5("lucarossi@gmail.com")."'") or die(mysql_error());

			//Chiudo la connessione al DB
			mysql_close($access);
      echo "Test completato!\n";
  }

  public function testProfiloPersonaleDottore()
  {
      echo "\nprofiloPersonaleDottore(\"c8e3243a4dc262e1400ee5a371ae0537\")\n";
      $result = json_decode($this->account->profiloPersonaleDottore("c8e3243a4dc262e1400ee5a371ae0537"), TRUE);

      assert($result['data']['Mail'] == "alfonso.menichino@gmail.com");
      assert($result['data']['Nome'] == "Alfonso");
      assert($result['data']['Cognome'] == "Menichino");
      echo "Test completato!\n";
  }

  public function testProfiloPersonalePaziente()
  {
      echo "\nprofiloPersonalePaziente(\"68d0a0716d8cb0cc79535ac8a3d1810a\")\n";
      $result = json_decode($this->account->profiloPersonalePaziente("68d0a0716d8cb0cc79535ac8a3d1810a"), TRUE);

      assert($result['data']['Mail'] == "volpea.93@gmail.com");
      assert($result['data']['Nome'] == "Alberto");
      assert($result['data']['Cognome'] == "Volpe");
      echo "Test completato!\n";
  }

  public function testModalitaSOS()
  {
      echo "\nmodalitaSOS(\"68d0a0716d8cb0cc79535ac8a3d1810a\")\n";
      $result = json_decode($this->account->modalitaSOS("68d0a0716d8cb0cc79535ac8a3d1810a"), TRUE);

      assert($result['data']['NumeroSOS1'] == "3460624046");
      echo "Test completato!\n";
  }

  public function testAggiornaProfiloPaziente()
  {
      echo "\naggiornaProfiloPaziente('68d0a0716d8cb0cc79535ac8a3d1810a','3476760352', 'allergia', '3460624046', '3476760352', 'Via Santi', 'Giffoni Valle Piana (SA)')\n";
      $this->account->aggiornaProfiloPaziente('68d0a0716d8cb0cc79535ac8a3d1810a','3476760352', 'allergia', '3460624046', '3476760352', 'Via Santi', 'Giffoni Valle Piana (SA)');

      $query = 'SELECT Mail, Nome, Cognome, Sesso, CodiceFiscale, Cellulare, CittaResidenza, DettagliClinici, Residenza, DataNascita, LuogoNascita, NumeroSOS1, NumeroSOS2
                FROM Utenti, Pazienti WHERE Utenti.UserID=Pazienti.UserID AND Utenti.UserID = \'68d0a0716d8cb0cc79535ac8a3d1810a\'';

      $result = mysql_query($query) or die(mysql_error());
      $record = mysql_fetch_array($result);

      assert('allergia' == $record['DettagliClinici']);
      assert('Giffoni Valle Piana (SA)' == $record['CittaResidenza']);
      assert('Via Santi' == $record['Residenza']);
      echo "Test completato!\n";
  }

  public function testAggiornaProfiloDottore()
  {
      echo "\nAggiornaProfiloDottore('c8e3243a4dc262e1400ee5a371ae0537','Via Fratelli D'italia', 'SA','Salerno','089884101','Medico di Base')\n";
      $this->account->AggiornaProfiloDottore('c8e3243a4dc262e1400ee5a371ae0537','Via Fratelli D\'italia', 'SA','Salerno','089884101','Medico di Base');

      $query = 'SELECT Mail, Nome, Cognome, Sesso, IDAlbo, Citta, Provincia, Specializzazione, IndirizzoStudio, TelefonoStudio
                FROM Utenti, Dottori WHERE Utenti.UserID=Dottori.UserID AND Utenti.UserID = \'c8e3243a4dc262e1400ee5a371ae0537\'';

      $result = mysql_query($query) or die(mysql_error());
      $record = mysql_fetch_array($result);

      assert('089884101' == $record['TelefonoStudio']);
      echo "Test completato!\n";
  }
}

$test = new AccountManagerTest();
$test->testLogin();
$test->testControlloCredenziali();
$test->testRegistraAccountPaziente();
$test->testRegistraAccountDottore();
$test->testProfiloPersonaleDottore();
$test->testProfiloPersonalePaziente();
$test->testModalitaSOS();
$test->testAggiornaProfiloPaziente();
$test->testAggiornaProfiloDottore();
?>
