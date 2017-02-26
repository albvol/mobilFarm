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
  case "visualizzaRubricaDottori":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((isset($_COOKIE['UserID'])) && ($_COOKIE['Type'] == "Paziente"))
    {
       try
       {
          /* Connetto al Database Server */
           $access = mysql_connect($directoryServer, $username, $password) or die(mysql_error());
          if($access)
          {
            /* Seleziono il Database */
            mysql_select_db($dbName);

            /* Parsing dei dati  per evitare la MYSQL Injection */
            $userID = mysql_real_escape_string(strip_tags($_COOKIE['UserID']));

            /* Preparo ed eseguo la query */
            $query = 'SELECT Utenti.UserID, Utenti.Nome, Utenti.Cognome, Dottori.Specializzazione
            FROM Seguire, Dottori, Utenti
            WHERE Utenti.UserID = Dottori.UserID
             AND Dottori.UserID = Seguire.DottoreID AND Seguire.PazienteID = \''.$userID.'\'
             ORDER BY Utenti.Cognome ASC';
            $result = mysql_query($query) or die(mysql_error());

            //Se il paziente non segue alcun dottore...
            if(mysql_num_rows($result) == 0)
            {
              echo json_encode(array(
                   'status' => 'standard',
                   'data' => array(
                     'count' => 0
                   )));
            }else{

              $i = -1;
              while ($record=mysql_fetch_assoc($result)){

                  $i++;
                  $data[$i] = array(
                    'UserID' => $record['UserID'],
                    'Nome' => $record['Nome'],
                    'Cognome' => $record['Cognome'],
                    'Specializzazione' => $record['Specializzazione']);
              }

              echo json_encode(array(
                   'status' => 'standard',
                   'data' => array(
                     'count' => mysql_num_rows($result),
                     'lista_dottori' => $data
                   )));
            }

            //Chiudo la connessione al DB
            mysql_close($access);

          }else throw new Exception("Non riesco a connettermi al database");

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
  case "visualizzaRubricaPazienti":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((isset($_COOKIE['UserID'])) && ($_COOKIE['Type'] == "Dottore"))
    {
       try
       {
          /* Connetto al Database Server */
           $access = mysql_connect($directoryServer, $username, $password) or die(mysql_error());
          if($access)
          {
            /* Seleziono il Database */
            mysql_select_db($dbName);

            /* Parsing dei dati  per evitare la MYSQL Injection */
            $userID = mysql_real_escape_string(strip_tags($_COOKIE['UserID']));

            /* Preparo ed eseguo la query */
            $query = 'SELECT Utenti.UserID, Utenti.Nome, Utenti.Cognome, Pazienti.CittaResidenza
                      FROM Seguire, Pazienti, Utenti
                      WHERE Utenti.UserID = Pazienti.UserID
                      AND Pazienti.UserID = Seguire.PazienteID AND Seguire.DottoreID = \''.$userID.'\'
                      ORDER BY Utenti.Cognome ASC';
            $result = mysql_query($query) or die(mysql_error());

            //Se il dottore non viene seguito da nessun paziente...
            if(mysql_num_rows($result) == 0)
            {
              echo json_encode(array(
                   'status' => 'standard',
                   'data' => array(
                     'count' => 0
                   )));
            }else{

              $i = -1;
              while ($record=mysql_fetch_assoc($result)){

                  $i++;
                  $data[$i] = array(
                    'UserID' => $record['UserID'],
                    'Nome' => $record['Nome'],
                    'Cognome' => $record['Cognome'],
                    'CittaResidenza' => $record['CittaResidenza']);
              }

              echo json_encode(array(
                   'status' => 'standard',
                   'data' => array(
                     'count' => mysql_num_rows($result),
                     'lista_pazienti' => $data
                   )));
            }

            //Chiudo la connessione al DB
            mysql_close($access);
          }else throw new Exception("Non riesco a connettermi al database");

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
  case "visualizzaProfiloPazienteSelezionato":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((isset($_COOKIE['UserID'])) && ($_COOKIE['Type'] == "Dottore"))
    {
       try
       {
          /* Connetto al Database Server */
           $access = mysql_connect($directoryServer, $username, $password) or die(mysql_error());
          if($access)
          {
            /* Seleziono il Database */
            mysql_select_db($dbName);

            /* Parsing dei dati  per evitare la MYSQL Injection */
            $pazienteID = mysql_real_escape_string(strip_tags($_REQUEST['data']['PazienteID']));

            /* Preparo ed eseguo la query */
            $query = 'SELECT Utenti.UserID, Utenti.Mail, Utenti.Nome, Utenti.Cognome, Utenti.Sesso, Pazienti.CodiceFiscale, Pazienti.Cellulare, Pazienti.DettagliClinici, Pazienti.LuogoNascita, Pazienti.DataNascita, Pazienti.Residenza, Pazienti.CittaResidenza FROM Pazienti, Utenti WHERE Utenti.UserID = \''.$pazienteID.'\' AND Pazienti.UserID = \''.$pazienteID.'\'';
            $result = mysql_query($query) or die(mysql_error());
            $record = mysql_fetch_array($result);

            //Se non sono presenti informazioni relative al paziente...
            if(mysql_num_rows($result) == 0)
              throw new Exception("Non sono presenti i dati relativi al paziente "+$pazienteID);
            else{
              echo json_encode(array(
                   'status' => 'standard',
                   'data' => array(
                      "UserID" => $record['UserID'],
                      "Mail" => $record['Mail'],
                      "Nome" => $record['Nome'],
                      "Cognome" => $record['Cognome'],
                      "Sesso" => $record['Sesso'],
                      "CodiceFiscale" => $record['CodiceFiscale'],
                      "Cellulare" => $record['Cellulare'],
                      "DettagliClinici" => $record['DettagliClinici'],
                      "LuogoNascita" => $record['LuogoNascita'],
                      "DataNascita" => $record['DataNascita'],
                      "Residenza" => $record['Residenza'],
                      "CittaResidenza" => $record['CittaResidenza']
                   )
                ));
            }

            //Chiudo la connessione al DB
            mysql_close($access);
          }else throw new Exception("Non riesco a connettermi al database");

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
  case "visualizzaProfiloDottoreSelezionato":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((isset($_COOKIE['UserID'])) && ($_COOKIE['Type'] == "Paziente"))
    {
       try
       {
          /* Connetto al Database Server */
           $access = mysql_connect($directoryServer, $username, $password) or die(mysql_error());
          if($access)
          {
            /* Seleziono il Database */
            mysql_select_db($dbName);

            /* Parsing dei dati  per evitare la MYSQL Injection */
            $dottoreID = mysql_real_escape_string(strip_tags($_REQUEST['data']['DottoreID']));

            /* Preparo ed eseguo la query */
            $query = 'SELECT Utenti.UserID, Utenti.Mail, Utenti.Nome, Utenti.Cognome, Utenti.Sesso, Dottori.IDAlbo, Dottori.Citta, Dottori.Provincia, Dottori.Specializzazione, Dottori.IndirizzoStudio, Dottori.TelefonoStudio FROM Dottori, Utenti WHERE Utenti.UserID = \''.$dottoreID.'\' AND Dottori.UserID = \''.$dottoreID.'\'';
            $result = mysql_query($query) or die(mysql_error());
            $record = mysql_fetch_array($result);

            //Se non sono presenti informazioni relative al dottore...
            if(mysql_num_rows($result) == 0)
              throw new Exception("Non sono presenti i dati relativi al dottore "+$dottoreID);
            else{
              echo json_encode(array(
                   'status' => 'standard',
                   'data' => array(
                      "UserID" => $record['UserID'],
                      "Mail" => $record['Mail'],
                      "Nome" => $record['Nome'],
                      "Cognome" => $record['Cognome'],
                      "Sesso" => $record['Sesso'],
                      "IDAlbo" => $record['IDAlbo'],
                      "Citta" => $record['Citta'],
                      "Provincia" => $record['Provincia'],
                      "Specializzazione" => $record['Specializzazione'],
                      "IndirizzoStudio" => $record['IndirizzoStudio'],
                      "TelefonoStudio" => $record['TelefonoStudio']
                   )
                ));
            }

            //Chiudo la connessione al DB
            mysql_close($access);
          }else throw new Exception("Non riesco a connettermi al database");

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
  case "rimuoviPaziente":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((isset($_COOKIE['UserID'])) && ($_COOKIE['Type'] == "Dottore"))
    {
       try
       {
          /* Connetto al Database Server */
           $access = mysql_connect($directoryServer, $username, $password) or die(mysql_error());
          if($access)
          {
            /* Seleziono il Database */
            mysql_select_db($dbName);

            /* Parsing dei dati  per evitare la MYSQL Injection */
            $dottoreID = mysql_real_escape_string(strip_tags($_COOKIE['UserID']));
            $pazienteID = mysql_real_escape_string(strip_tags($_REQUEST['data']['PazienteID']));

            /* Preparo ed eseguo la query */
            $query = 'DELETE FROM Seguire WHERE PazienteID = \''.$pazienteID.'\' AND DottoreID = \''.$dottoreID.'\'';
            mysql_query($query) or die(mysql_error());

            echo json_encode(array(
                 'status' => 'standard'
                 ));

            //Chiudo la connessione al DB
            mysql_close($access);

          }else throw new Exception("Non riesco a connettermi al database");

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
  case "rimuoviDottore":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((isset($_COOKIE['UserID'])) && ($_COOKIE['Type'] == "Paziente"))
    {
       try
       {
          /* Connetto al Database Server */
          $access = mysql_connect($directoryServer, $username, $password) or die(mysql_error());
          if($access)
          {
            /* Seleziono il Database */
            mysql_select_db($dbName);

            /* Parsing dei dati  per evitare la MYSQL Injection */
            $pazienteID = mysql_real_escape_string(strip_tags($_COOKIE['UserID']));
            $dottoreID = mysql_real_escape_string(strip_tags($_REQUEST['data']['DottoreID']));

            /* Preparo ed eseguo la query */
            $query = 'DELETE FROM Seguire WHERE PazienteID = \''.$pazienteID.'\' AND DottoreID = \''.$dottoreID.'\'';
            mysql_query($query) or die(mysql_error());

            echo json_encode(array(
                 'status' => 'standard'
                 ));

           //Chiudo la connessione al DB
           mysql_close($access);

          }else throw new Exception("Non riesco a connettermi al database");

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
  case "aggiungiDottore":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((isset($_COOKIE['UserID'])) && ($_COOKIE['Type'] == "Paziente"))
    {
       try
       {
          /* Connetto al Database Server */
           $access = mysql_connect($directoryServer, $username, $password) or die(mysql_error());
          if($access)
          {
            /* Seleziono il Database */
            mysql_select_db($dbName);

            /* Parsing dei dati  per evitare la MYSQL Injection */
            $pazienteID = mysql_real_escape_string(strip_tags($_COOKIE['UserID']));
            $dottoreID = mysql_real_escape_string(strip_tags($_REQUEST['data']['DottoreID']));

            /* Preparo ed eseguo la query */
            $query = 'INSERT INTO Seguire (PazienteID, DottoreID) VALUES (\''.$pazienteID.'\', \''.$dottoreID.'\')';
            mysql_query($query) or die(mysql_error());

            echo json_encode(array(
                 'status' => 'standard'
                 ));

            //Chiudo la connessione al DB
            mysql_close($access);

          }else throw new Exception("Non riesco a connettermi al database");

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
  case "ricercaDottore":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((isset($_COOKIE['UserID'])) && ($_COOKIE['Type'] == "Paziente"))
    {
       try
       {
          /* Connetto al Database Server */
           $access = mysql_connect($directoryServer, $username, $password) or die(mysql_error());
          if($access)
          {
            /* Seleziono il Database */
            mysql_select_db($dbName);

            /* Parsing dei dati  per evitare la MYSQL Injection */
            $keyword = mysql_real_escape_string(strip_tags($_REQUEST['data']['Keyword']));

            /* Preparo ed eseguo la query */
            $query = 'SELECT Utenti.UserID, Utenti.Nome, Utenti.Cognome, Dottori.Specializzazione
                      FROM Utenti, Dottori
                      WHERE Utenti.Tipologia = "Dottore" AND
                      	    Utenti.UserID = Dottori.UserID AND
                            Utenti.Cognome Like "'.$keyword.'%"';
            $result = mysql_query($query) or die(mysql_error());

            //Se la chiave di ricerca non corrisponde a nessun dottore...
            if(mysql_num_rows($result) == 0)
            {
              echo json_encode(array(
                   'status' => 'standard',
                   'data' => array(
                     'count' => 0
                   )));
            }else{

              $i = -1;
              while ($record=mysql_fetch_assoc($result)){

                  $i++;
                  $data[$i] = array(
                    'UserID' => $record['UserID'],
                    'Nome' => $record['Nome'],
                    'Cognome' => $record['Cognome'],
                    'Specializzazione' => $record['Specializzazione']);
              }

              echo json_encode(array(
                   'status' => 'standard',
                   'data' => array(
                     'count' => mysql_num_rows($result),
                     'lista_dottori' => $data
                   )));
            }

            //Chiudo la connessione al DB
            mysql_close($access);

           }else throw new Exception("Non riesco a connettermi al database");

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

}


Class RubricaManager{
    public function visualizzaRubricaDottori($userID)
    {

    }
    public function  visualizzaRubricaPazienti($userID)
    {

    }
    public function visualizzaProfiloPazienteSelezionato($pazienteID)
    {

    }
    public function visualizzaProfiloDottoreSelezionato($dottoreID)
    {

    }
    public function rimuoviPaziente($dottoreID,$pazienteID)
    {

    }
    public function  rimuoviDottore($pazienteID,$dottoreID)
    {

    }
    public function aggiungiDottore($pazienteID,$dottoreID)
    {

    }
    public function  ricercaDottore($keyword)
    {

    }
}
 ?>
