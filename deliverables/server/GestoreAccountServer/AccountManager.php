<?php

header('Content-Type: application/json');
date_default_timezone_set("UTC");

//decode the request with json (for the requests form the mobile app)
$_REQUEST = json_decode(file_get_contents('php://input'), true);

$directoryServer = "mobilfarm.cqplmnbzeyqt.us-east-1.rds.amazonaws.com:3306";
$username = "mobilFarm";
$password = "mobilFarm";
$dbName = "mobilFarm";

switch($_REQUEST['action'])
{
  case "login":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((!isset($_COOKIE['UserID'])) && (!isset($_COOKIE['Type'])))
    {
       try
       {
         $manager = new AccountManager();
         echo $manager->login($_REQUEST['data']['Mail'], $_REQUEST['data']['Password']);
         $manager->close();

         }catch(Exception $e){
           echo json_encode(array(
                    'status' => 'error',
                    'exception' => 'ResponseErrorException',
                    'message' => $e->getMessage()
           ));
         }

     }else{
        echo json_encode(array(
                 'status' => 'error',
                 'exception' => 'ActionNotAuthorizedException'
        ));
     }
     break;
  }
  case "logout":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if(isset($_COOKIE['UserID']) && (($_COOKIE['Type'] == "Dottore") || ($_COOKIE['Type'] == "Paziente")))
    {
       try
       {
            //Elimino il cookie
            unset($_COOKIE['UserID']);
            unset($_COOKIE['Type']);
            setcookie('UserID', null, -1, '/');
            setcookie('Type', null, -1, '/');

       }catch(Exception $e){
         echo json_encode(array(
                  'status' => 'error',
                  'exception' => 'ResponseErrorException',
                  'message' => $e->getMessage()
         ));
         break;
       }

       echo json_encode(array(
                'status' => 'standard'
              ));
     }else{
        echo json_encode(array(
                 'status' => 'error',
                 'exception' => 'ActionNotAuthorizedException'
        ));
     }

     break;
  }
  case "controlloCredenziali":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((!isset($_COOKIE['UserID'])) && (!isset($_COOKIE['Type'])))
    {
       try
       {
         $manager = new AccountManager();
         $value = $manager->controlloCredenziali($_REQUEST['data']['Mail']);
         $manager->close();

         }catch(Exception $e){
           echo json_encode(array(
                    'status' => 'error',
                    'exception' => 'ResponseErrorException',
                    'message' => $e->getMessage()
           ));
         }

         echo json_encode(array(
                  'status' => 'standard',
                  'data' => array(
                    'mail_presente' => $value
                  )));
     }else{
        echo json_encode(array(
                 'status' => 'error',
                 'exception' => 'ActionNotAuthorizedException'
        ));
     }

     break;
  }
  case "registraAccountPaziente":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((!isset($_COOKIE['UserID'])) && (!isset($_COOKIE['Type'])))
    {
       try
       {
           $manager = new AccountManager();
           $manager->registraAccountPaziente($_REQUEST['data']['Mail'], $_REQUEST['data']['Password'], $_REQUEST['data']['Nome'], $_REQUEST['data']['Cognome'], $_REQUEST['data']['Sesso'], $_REQUEST['data']['Tipologia'], $_REQUEST['data']['CodiceFiscale'], $_REQUEST['data']['Cellulare'], $_REQUEST['data']['DettagliClinici'], $_REQUEST['data']['LuogoNascita'], $_REQUEST['data']['DataNascita'], $_REQUEST['data']['Residenza'], $_REQUEST['data']['CittaResidenza']);
           $manager->close();

         }catch(Exception $e){
           echo json_encode(array(
                    'status' => 'error',
                    'exception' => 'ResponseErrorException',
                    'message' => $e->getMessage()
           ));
           break;
         }

         echo json_encode(array(
                  'status' => 'standard'
                ));
     }else{
        echo json_encode(array(
                 'status' => 'error',
                 'exception' => 'ActionNotAuthorizedException'
        ));
     }

     break;
  }
  case "registraAccountDottore":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((!isset($_COOKIE['UserID'])) && (!isset($_COOKIE['Type'])))
    {
       try
       {
         $manager = new AccountManager();
         $manager->registraAccountDottore($_REQUEST['data']['Mail'], $_REQUEST['data']['Password'], $_REQUEST['data']['Nome'], $_REQUEST['data']['Cognome'], $_REQUEST['data']['Sesso'], $_REQUEST['data']['Tipologia'], $_REQUEST['data']['IDAlbo'], $_REQUEST['data']['Citta'], $_REQUEST['data']['Provincia'], $_REQUEST['data']['Specializzazione'], $_REQUEST['data']['IndirizzoStudio'], $_REQUEST['data']['TelefonoStudio']);
         $manager->close();

         }catch(Exception $e){
           echo json_encode(array(
                    'status' => 'error',
                    'exception' => 'ResponseErrorException',
                    'message' => $e->getMessage()
           ));
           break;
         }

         echo json_encode(array(
                  'status' => 'standard'
                ));
     }else{
        echo json_encode(array(
                 'status' => 'error',
                 'exception' => 'ActionNotAuthorizedException'
        ));
     }

     break;
  }
  case "profiloPersonaleDottore":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((isset($_COOKIE['UserID'])  && ($_COOKIE['Type'] == "Dottore")))
    {
       try
       {
         $manager = new AccountManager();
         echo $manager->profiloPersonaleDottore($_COOKIE['UserID']);
         $manager->close();

         }catch(Exception $e){
           echo json_encode(array(
                    'status' => 'error',
                    'exception' => 'ResponseErrorException',
                    'message' => $e->getMessage()
           ));

         }
     }else{
        echo json_encode(array(
                 'status' => 'error',
                 'exception' => 'ActionNotAuthorizedException'
        ));
     }

     break;
  }
  case "profiloPersonalePaziente":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((isset($_COOKIE['UserID'])  && ($_COOKIE['Type'] == "Paziente")))
    {
       try
       {
           $manager = new AccountManager();
           echo $manager->profiloPersonalePaziente($_COOKIE['UserID']);
           $manager->close();

         }catch(Exception $e){
           echo json_encode(array(
                    'status' => 'error',
                    'exception' => 'ResponseErrorException',
                    'message' => $e->getMessage()
           ));
           break;
         }
     }else{
        echo json_encode(array(
                 'status' => 'error',
                 'exception' => 'ActionNotAuthorizedException'
        ));
     }

     break;
  }
  case "modalitaSOS":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((isset($_COOKIE['UserID'])  && ($_COOKIE['Type'] == "Paziente")))
    {
       try
       {
         $manager = new AccountManager();
         echo $manager->modalitaSOS($_COOKIE['UserID']);
         $manager->close();

         }catch(Exception $e){
           echo json_encode(array(
                    'status' => 'error',
                    'exception' => 'ResponseErrorException',
                    'message' => $e->getMessage()
           ));
           break;
         }

     }else{
        echo json_encode(array(
                 'status' => 'error',
                 'exception' => 'ActionNotAuthorizedException'
        ));
     }

     break;
  }
  case "aggiornaProfiloPaziente":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((isset($_COOKIE['UserID'])  && ($_COOKIE['Type'] == "Paziente")))
    {
       try
       {

         $manager = new AccountManager();
         $manager->aggiornaProfiloPaziente($_COOKIE['UserID'], $_REQUEST['data']['Cellulare'], $_REQUEST['data']['DettagliClinici'], $_REQUEST['data']['NumeroSOS1'], $_REQUEST['data']['NumeroSOS2'], $_REQUEST['data']['Residenza'], $_REQUEST['data']['CittaResidenza']);
         $manager->close();

         }catch(Exception $e){
           echo json_encode(array(
                    'status' => 'error',
                    'exception' => 'ResponseErrorException',
                    'message' => $e->getMessage()
           ));
           break;
         }

         echo json_encode(array(
                  'status' => 'standard',
                  ));
     }else{
        echo json_encode(array(
                 'status' => 'error',
                 'exception' => 'ActionNotAuthorizedException'
        ));
     }

     break;
  }
  case "aggiornaProfiloDottore":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((isset($_COOKIE['UserID'])  && ($_COOKIE['Type'] == "Dottore")))
    {
       try
       {

          $manager = new AccountManager();
          $manager->aggiornaProfiloDottore($_COOKIE['UserID'], $_REQUEST['data']['IndirizzoStudio'], $_REQUEST['data']['Provincia'], $_REQUEST['data']['Citta'], $_REQUEST['data']['TelefonoStudio'], $_REQUEST['data']['Specializzazione']);
          $manager->close();


         }catch(Exception $e){
           echo json_encode(array(
                    'status' => 'error',
                    'exception' => 'ResponseErrorException',
                    'message' => $e->getMessage()
           ));
           break;
         }

         echo json_encode(array(
                  'status' => 'standard',
                  ));
     }else{
        echo json_encode(array(
                 'status' => 'error',
                 'exception' => 'ActionNotAuthorizedException'
        ));
     }

     break;
  }
}


Class AccountManager{

    private $access;
    private $directoryServer = "mobilfarm.cqplmnbzeyqt.us-east-1.rds.amazonaws.com:3306";
    private $username = "mobilFarm";
    private $password = "mobilFarm";
    private $dbName = "mobilFarm";

    public function __construct()
    {
      $this->access = mysql_connect($this->directoryServer, $this->username, $this->password) or die(mysql_error());
      if(!$this->access) throw new Exception("Non riesco a connettermi al database");

      /* Seleziono il Database */
      mysql_select_db($this->dbName);
    }

    public function close()
    {
				//Chiudo la connessione al DB
				mysql_close($this->access);
    }

    public function login($mail, $password)
    {
        /* Parsing dei dati ricevuti per evitare la MYSQL Injection */
        $mail = mysql_real_escape_string(strip_tags($mail));
        $password = mysql_real_escape_string(strip_tags($password));

        /* Preparo ed eseguo la query */
        $query = 'SELECT UserID, Nome, Cognome, Tipologia FROM Utenti WHERE Mail=\''.$mail.'\' AND Password=\''.$password.'\'';
        $result = mysql_query($query);
        if (!$result) {
          die('Error: ' . mysql_error($this->access));
        }

        $record = mysql_fetch_array($result);

        /* Se è stato restituito un record... */
        if(mysql_num_rows($result) == 1)
        {
            //Creo il cookie
            setcookie("UserID", $record['UserID'], time() + (86400 * 30), "/");
            setcookie("Type", $record['Tipologia'], time() + (86400 * 30), "/");

            $value = true;
         }else $value = false;

          //stampa -> invia al client
          return json_encode(array(
                   'status' => 'standard',
                   'data' => array(
                     'credenziali_corrette' => $value,
                     'UserID' => $record['UserID'],
                     'Nome' => $record['Nome'],
                     'Cognome' => $record['Cognome'],
                     'Type' => $record['Tipologia']
                   )));
    }

    public function controlloCredenziali($mail)
    {
        /* Parsing dei dati ricevuti per evitare la MYSQL Injection */
        $mail = mysql_real_escape_string(strip_tags($mail));

        /* Preparo ed eseguo la query */
        $query = 'SELECT COUNT(*) FROM Utenti WHERE Mail=\''.$mail.'\'';
        $result = mysql_query($query) or die(mysql_error());
        $record = mysql_fetch_row($result);

        /* Se non esiste una mail uguale... */
        if($record[0] == 0) $value = false;
        else $value = true;

        return $value;
    }

    public function registraAccountPaziente($mail,$password, $nome,$cognome,$sesso,$tipologia,$codiceFiscale,$cellulare,$dettagliClinici,$luogoNascita,$dataNascita,$residenza,$cittaResidenza)
    {
      /* Parsing dei dati ricevuti per evitare la MYSQL Injection */
      $mail = mysql_real_escape_string(strip_tags($mail));
      $password = mysql_real_escape_string(strip_tags($password));
      $userID = mysql_real_escape_string(strip_tags(md5($mail)));
      $nome = mysql_real_escape_string(strip_tags($nome));
      $cognome = mysql_real_escape_string(strip_tags($cognome));
      $sesso = mysql_real_escape_string(strip_tags($sesso));
      $tipologia = mysql_real_escape_string(strip_tags($tipologia));

      $codiceFiscale = mysql_real_escape_string(strip_tags($codiceFiscale));
      $cellulare = mysql_real_escape_string(strip_tags($cellulare));
      $dettagliClinici = mysql_real_escape_string(strip_tags($dettagliClinici));
      $luogoNascita = mysql_real_escape_string(strip_tags($luogoNascita));
      $dataNascita = mysql_real_escape_string(strip_tags($dataNascita));
      $residenza = mysql_real_escape_string(strip_tags($residenza));
      $cittaResidenza = mysql_real_escape_string(strip_tags($cittaResidenza));

      /* Preparo ed eseguo la prima query per registrare l'utente*/
      $query = 'INSERT INTO Utenti (UserID, Mail, Nome, Cognome, Password, Sesso, Tipologia) VALUES (\''.$userID.'\',\''.$mail.'\',\''.$nome.'\',\''.$cognome.'\',\''.$password.'\',\''.$sesso.'\',\''.$tipologia.'\')';

      mysql_query($query) or die(mysql_error());

      /* Preparo ed eseguo la seconda query per registrare le info del paziente*/
      $query = 'INSERT INTO Pazienti (UserID, CodiceFiscale, Cellulare, DettagliClinici, LuogoNascita, DataNascita, Residenza, CittaResidenza) VALUES (\''.$userID.'\',\''.$codiceFiscale.'\',\''.$cellulare.'\',\''.$dettagliClinici.'\',\''.$luogoNascita.'\',\''.$dataNascita.'\',\''.$residenza.'\',\''.$cittaResidenza.'\')';

      mysql_query($query) or die(mysql_error());
    }

    public function registraAccountDottore($mail,$password,$nome,$cognome,$sesso,$tipologia,$IDAlbo,$citta,$provincia,$specializzazione,$indirizzoStudio,$telefonoStudio)
    {
      /* Parsing dei dati ricevuti per evitare la MYSQL Injection */
      $mail = mysql_real_escape_string(strip_tags($mail));
      $password = mysql_real_escape_string(strip_tags($password));
      $userID = mysql_real_escape_string(strip_tags(md5($mail)));
      $nome = mysql_real_escape_string(strip_tags($nome));
      $cognome = mysql_real_escape_string(strip_tags($cognome));
      $sesso = mysql_real_escape_string(strip_tags($sesso));
      $tipologia = mysql_real_escape_string(strip_tags($tipologia));

      $IDAlbo = mysql_real_escape_string(strip_tags($IDAlbo));
      $citta = mysql_real_escape_string(strip_tags($citta));
      $provincia = mysql_real_escape_string(strip_tags($provincia));
      $specializzazione = mysql_real_escape_string(strip_tags($specializzazione));
      $indirizzoStudio = mysql_real_escape_string(strip_tags($indirizzoStudio));
      $telefonoStudio = mysql_real_escape_string(strip_tags($telefonoStudio));

      /* Preparo ed eseguo la prima query per registrare l'utente*/
      $query = 'INSERT INTO Utenti (UserID, Mail, Nome, Cognome, Password, Sesso, Tipologia) VALUES (\''.$userID.'\',\''.$mail.'\',\''.$nome.'\',\''.$cognome.'\',\''.$password.'\',\''.$sesso.'\',\''.$tipologia.'\')';

      mysql_query($query) or die(mysql_error());

      /* Preparo ed eseguo la seconda query per registrare le info del dottore*/
      $query = 'INSERT INTO Dottori (UserID, IDAlbo, Citta, Provincia, Specializzazione, IndirizzoStudio, TelefonoStudio) VALUES (\''.$userID.'\',\''.$IDAlbo.'\',\''.$citta.'\',\''.$provincia.'\',\''.$specializzazione.'\',\''.$indirizzoStudio.'\',\''.$telefonoStudio.'\')';

      mysql_query($query) or die(mysql_error());
    }

    public function profiloPersonaleDottore($userID)
    {
      /* Parsing dei dati ricevuti per evitare la MYSQL Injection */
      $userID = mysql_real_escape_string(strip_tags($userID));

      /* Preparo ed eseguo la query */
      $query = 'SELECT Mail, Nome, Cognome, Sesso, IDAlbo, Citta, Provincia, Specializzazione, IndirizzoStudio, TelefonoStudio
                FROM Utenti, Dottori WHERE Utenti.UserID=Dottori.UserID AND Utenti.UserID = \''.$userID.'\'';
      $result = mysql_query($query) or die(mysql_error());
      $record = mysql_fetch_array($result);

      return json_encode(array(
               'status' => 'standard',
               'data' => array(
                 'Mail' => $record['Mail'],
                 'Nome' => $record['Nome'],
                 'Cognome' => $record['Cognome'],
                 'Sesso' => $record['Sesso'],
                 'IDAlbo' => $record['IDAlbo'],
                 'Citta' => $record['Citta'],
                 'Provincia' => $record['Provincia'],
                 'Specializzazione' => $record['Specializzazione'],
                 'IndirizzoStudio' => $record['IndirizzoStudio'],
                 'TelefonoStudio' => $record['TelefonoStudio']
               )
             ));
    }

    public function profiloPersonalePaziente($userID)
    {
      /* Parsing dei dati ricevuti per evitare la MYSQL Injection */
      $userID = mysql_real_escape_string(strip_tags($userID));

      /* Preparo ed eseguo la query */
      $query = 'SELECT Mail, Nome, Cognome, Sesso, CodiceFiscale, Cellulare, CittaResidenza, DettagliClinici, Residenza, DataNascita, LuogoNascita, NumeroSOS1, NumeroSOS2
                FROM Utenti, Pazienti WHERE Utenti.UserID=Pazienti.UserID AND Utenti.UserID = \''.$userID.'\'';
      $result = mysql_query($query) or die(mysql_error());
      $record = mysql_fetch_array($result);

      return json_encode(array(
               'status' => 'standard',
               'data' => array(
                 'Mail' => $record['Mail'],
                 'Nome' => $record['Nome'],
                 'Cognome' => $record['Cognome'],
                 'Sesso' => $record['Sesso'],
                 'CodiceFiscale' => $record['CodiceFiscale'],
                 'Cellulare' => $record['Cellulare'],
                 'DettagliClinici' => $record['DettagliClinici'],
                 'CittaResidenza' => $record['CittaResidenza'],
                 'Residenza' => $record['Residenza'],
                 'DataNascita' => $record['DataNascita'],
                 'LuogoNascita' => $record['LuogoNascita'],
                 'NumeroSOS1' => $record['NumeroSOS1'],
                 'NumeroSOS2' => $record['NumeroSOS2']
               )
             ));
    }

    public function modalitaSOS($userID)
    {
      /* Parsing dei dati ricevuti per evitare la MYSQL Injection */
      $userID = mysql_real_escape_string(strip_tags($userID));

      /* Preparo ed eseguo la query */
      $query = 'SELECT NumeroSOS1, NumeroSOS2
                FROM Utenti, Pazienti WHERE Utenti.UserID=Pazienti.UserID AND Utenti.UserID = \''.$userID.'\'';
      $result = mysql_query($query) or die(mysql_error());
      $record = mysql_fetch_array($result);

      return json_encode(array(
               'status' => 'standard',
               'data' => array(
                 'NumeroSOS1' => $record['NumeroSOS1'],
                 'NumeroSOS2' => $record['NumeroSOS2']
               )
             ));
    }

    public function aggiornaProfiloPaziente($userID,$cellulare,$dettagliClinici,$numeroSOS1,$numeroSOS2,$residenza,$cittaResidenza)
    {
      /* Parsing dei dati ricevuti per evitare la MYSQL Injection */
      $userID = mysql_real_escape_string(strip_tags($userID));
      $cellulare = mysql_real_escape_string(strip_tags($cellulare));
      $dettagliClinici = mysql_real_escape_string(strip_tags($dettagliClinici));
      $numeroSOS1 = mysql_real_escape_string(strip_tags($numeroSOS1));
      $numeroSOS2 = mysql_real_escape_string(strip_tags($numeroSOS2));
      $residenza = mysql_real_escape_string(strip_tags($residenza));
      $cittaResidenza = mysql_real_escape_string(strip_tags($cittaResidenza));

      /* Preparo ed eseguo la query */
      $query = 'Update Pazienti
                SET Cellulare = \''.$cellulare.'\',
                    DettagliClinici = \''.$dettagliClinici.'\',
                    NumeroSOS1 = \''.$numeroSOS1.'\',
                    NumeroSOS2 = \''.$numeroSOS2.'\',
                    Residenza = \''.$residenza.'\',
                    CittaResidenza = \''.$cittaResidenza.'\'
                WHERE UserID = \''.$userID.'\'';

      mysql_query($query) or die(mysql_error());
    }
    public function aggiornaProfiloDottore($userID,$indirizzoStudio,$provincia,$citta,$telefonoStudio,$specializzazione)
    {
      /* Parsing dei dati ricevuti per evitare la MYSQL Injection */
      $userID = mysql_real_escape_string(strip_tags($userID));
      $indirizzoStudio = mysql_real_escape_string(strip_tags($indirizzoStudio));
      $provincia = mysql_real_escape_string(strip_tags($provincia));
      $citta = mysql_real_escape_string(strip_tags($citta));
      $telefonoStudio = mysql_real_escape_string(strip_tags($telefonoStudio));
      $specializzazione = mysql_real_escape_string(strip_tags($specializzazione));

      /* Preparo ed eseguo la query */
      $query = 'Update Dottori
                SET IndirizzoStudio = \''.$indirizzoStudio.'\',
                    Provincia = \''.$provincia.'\',
                    Citta = \''.$citta.'\',
                    TelefonoStudio = \''.$telefonoStudio.'\',
                    Specializzazione = \''.$specializzazione.'\'
                WHERE UserID = \''.$userID.'\'';

      mysql_query($query) or die(mysql_error());
    }
}
?>
