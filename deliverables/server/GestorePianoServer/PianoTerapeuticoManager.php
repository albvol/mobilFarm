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
  case "visualizzaPianiTerapeutici":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if(isset($_COOKIE['UserID']) && ($_COOKIE['Type'] == "Paziente"))
    {
       try
       {
          /* Connetto al Database Server */
          $access = mysql_connect($directoryServer, $username, $password) or die(mysql_error());
          if($access)
          {
            /* Seleziono il Database */
            mysql_select_db($dbName);

            /* Parsing dei dati ricevuti per evitare la MYSQL Injection */
            $userID = mysql_real_escape_string(strip_tags($_COOKIE['UserID']));

            /* Preparo ed eseguo la query */
            $query = 'SELECT PianiTerapeutici.IDTerapia, PianiTerapeutici.NomePiano, PianiTerapeutici.CompilatoDa, Utenti.Nome, Utenti.Cognome
                      FROM Utenti, PianiTerapeutici
                      WHERE PianiTerapeutici.CompilatoDa=Utenti.UserID AND PianiTerapeutici.InviatoA = \''.$userID.'\'';
            $result = mysql_query($query) or die(mysql_error());

            //Se l'utente non ha alcun ricevuto alcun piano terapeutico...
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
                    'IDTerapia' => $record['IDTerapia'],
                    'CompilatoDa' => $record['CompilatoDa'],
                    'NomePiano' => $record['NomePiano'],
                    'Nome' => $record['Nome'],
                    'Cognome' => $record['Cognome']);
              }

              echo json_encode(array(
                   'status' => 'standard',
                   'data' => array(
                     'count' => mysql_num_rows($result),
                     'lista_piani' => $data
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
  case "elencoAssunzioni":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if(isset($_COOKIE['UserID']) && ($_COOKIE['Type'] == "Paziente"))
    {
       try
       {
          /* Connetto al Database Server */
          $access = mysql_connect($directoryServer, $username, $password) or die(mysql_error());
          if($access)
          {
            /* Seleziono il Database */
            mysql_select_db($dbName);

            /* Parsing dei dati ricevuti per evitare la MYSQL Injection */
            $userID = mysql_real_escape_string(strip_tags($_COOKIE['UserID']));

            /* Preparo ed eseguo la query */
           $query = 'SELECT Prescrizioni.IDTerapia, Prescrizioni.IDFarmaco, NomePiano, Nome, Composizione, Dosaggio, DataInizio, DataTermine, OraInizio, Intervallo, Lunedi, Martedi, Mercoledi, Giovedi, Venerdi, Sabato, Domenica
                      FROM Prescrizioni, Farmaci, PianiTerapeutici
                      WHERE Prescrizioni.IDTerapia=PianiTerapeutici.IDTerapia
                      AND Prescrizioni.IDFarmaco=Farmaci.IDFarmaco
                      AND PianiTerapeutici.InviatoA=\''.$userID.'\'';

            $result = mysql_query($query) or die(mysql_error());

            //Se l'utente non deve assumere nessun farmaco...
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
                    'IDTerapia' => $record['IDTerapia'],
                    'IDFarmaco' => $record['IDFarmaco'],
                    'NomePiano' => $record['NomePiano'],
                    'Nome' => $record['Nome'],
                    'Composizione' => $record['Composizione'],
                    'Dosaggio' => $record['Dosaggio'],
                    'DataInizio' =>$record['DataInizio'],
                    'DataTermine' =>$record['DataTermine'],
                    'OraInizio' =>$record['OraInizio'],
                    'Intervallo' =>$record['Intervallo'],
                    'Lunedi' =>$record['Lunedi'],
                    'Martedi' =>$record['Martedi'],
                    'Mercoledi' =>$record['Mercoledi'],
                    'Giovedi' =>$record['Giovedi'],
                    'Venerdi' =>$record['Venerdi'],
                    'Sabato' =>$record['Sabato'],
                    'Domenica'=>$record['Domenica']);
              }

              echo json_encode(array(
                   'status' => 'standard',
                   'data' => array(
                     'count' => mysql_num_rows($result),
                     'lista_assunzioni' => $data
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
  case "visualizzaPianiTerapeuticiDiUnPaziente":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if(isset($_COOKIE['UserID']) && ($_COOKIE['Type'] == "Dottore"))
    {
       try
       {
          /* Connetto al Database Server */
          $access = mysql_connect($directoryServer, $username, $password) or die(mysql_error());
          if($access)
          {
            /* Seleziono il Database */
            mysql_select_db($dbName);

            /* Parsing dei dati ricevuti per evitare la MYSQL Injection */
            $pazienteID = mysql_real_escape_string(strip_tags($_REQUEST['data']['PazienteID']));

            /* Preparo ed eseguo la query */
            $query = 'SELECT PianiTerapeutici.IDTerapia, PianiTerapeutici.NomePiano, PianiTerapeutici.CompilatoDa, Utenti.Nome, Utenti.Cognome
                      FROM Utenti, PianiTerapeutici
                      WHERE PianiTerapeutici.CompilatoDa=Utenti.UserID AND PianiTerapeutici.InviatoA = \''.$pazienteID.'\'';
            $result = mysql_query($query) or die(mysql_error());

            //Se l'utente non ha alcun piano terapeutico...
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
                    'IDTerapia' => $record['IDTerapia'],
                    'NomePiano' => $record['NomePiano'],
                    'CompilatoDa' => $record['CompilatoDa'],
                    'Nome' => $record['Nome'],
                    'Cognome' => $record['Cognome']);
              }

              echo json_encode(array(
                   'status' => 'standard',
                   'data' => array(
                     'count' => mysql_num_rows($result),
                     'lista_piani' => $data
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
  case "visualizzaPianoTerapeutico":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if(isset($_COOKIE['UserID']) && (($_COOKIE['Type'] == "Paziente") || ($_COOKIE['Type'] == "Dottore")))
    {
       try
       {
          /* Connetto al Database Server */
          $access = mysql_connect($directoryServer, $username, $password) or die(mysql_error());
          if($access)
          {
            /* Seleziono il Database */
            mysql_select_db($dbName);

            /* Parsing dei dati ricevuti per evitare la MYSQL Injection */
            $IDTerapia = mysql_real_escape_string(strip_tags($_REQUEST['data']['IDTerapia']));

            /* Preparo ed eseguo la query */
            $query = 'SELECT Prescrizioni.IDFarmaco, Farmaci.Nome, Farmaci.Composizione
                      FROM Prescrizioni, PianiTerapeutici, Farmaci
                      WHERE PianiTerapeutici.IDTerapia=Prescrizioni.IDTerapia
                      AND Prescrizioni.IDFarmaco=Farmaci.IDFarmaco
                      AND PianiTerapeutici.IDTerapia = \''.$IDTerapia.'\'';
            $result = mysql_query($query) or die(mysql_error());

            //Se il piano non ha alcun farmaco...
            if(mysql_num_rows($result) == 0)
            {
              echo json_encode(array(
                   'status' => 'standard',
                   'data' => array(
                     'count' => 0
                   )));
            }else{

              $i = -1;
              while ($record=mysql_fetch_assoc($result))
              {
                  $i++;
                  $data[$i] = array(
                    'IDFarmaco' => $record['IDFarmaco'],
                    'Nome' => $record['Nome'],
                    'Composizione' => $record['Composizione']);
              }

              echo json_encode(array(
                   'status' => 'standard',
                   'data' => array(
                     'count' => mysql_num_rows($result),
                     'lista_farmaci' => $data
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
  case "visualizzaDettagliAssunzioneFarmaco":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if(isset($_COOKIE['UserID']) && (($_COOKIE['Type'] == "Paziente") || ($_COOKIE['Type'] == "Dottore")))
    {
       try
       {
          /* Connetto al Database Server */
          $access = mysql_connect($directoryServer, $username, $password) or die(mysql_error());
          if($access)
          {
            /* Seleziono il Database */
            mysql_select_db($dbName);

            /* Parsing dei dati ricevuti per evitare la MYSQL Injection */
            $IDTerapia = mysql_real_escape_string(strip_tags($_REQUEST['data']['IDTerapia']));
            $IDFarmaco = mysql_real_escape_string(strip_tags($_REQUEST['data']['IDFarmaco']));

            /* Preparo ed eseguo la query */
            $query = 'SELECT *
                      FROM Prescrizioni
                      WHERE IDFarmaco = \''.$IDFarmaco.'\' AND IDTerapia = \''.$IDTerapia.'\'';
            $result = mysql_query($query) or die(mysql_error());
            $record = mysql_fetch_array($result);


            echo json_encode(array(
                 'status' => 'standard',
                 'data' => array(
                     'Dosaggio' =>  $record['Dosaggio'],
                     'DataInizio' =>  $record['DataInizio'],
                     'DataTermine' => $record['DataTermine'],
                     'OraInizio' => $record['OraInizio'],
                     'Intervallo' => $record['Intervallo'],
                     'Lunedi' => $record['Lunedi'],
                     'Martedi' => $record['Martedi'],
                     'Mercoledi' => $record['Mercoledi'],
                     'Giovedi' => $record['Giovedi'],
                     'Venerdi' => $record['Venerdi'],
                     'Sabato' => $record['Sabato'],
                     'Domenica' => $record['Domenica']
                 )));

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
  case "creaPianoTerapeutico":
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
            $nomePiano = mysql_real_escape_string(strip_tags($_REQUEST['data']['NomePiano']));
            $inviatoA = mysql_real_escape_string(strip_tags($_REQUEST['data']['InviatoA']));

            /* Preparo ed eseguo la query */
            $query = 'INSERT INTO PianiTerapeutici (NomePiano, CompilatoDa, InviatoA) VALUES (\''.$nomePiano.'\', \''.$dottoreID.'\', \''.$inviatoA.'\')';
            mysql_query($query) or die(mysql_error());

            $query = 'SELECT IDTerapia FROM PianiTerapeutici WHERE NomePiano=\''.$nomePiano.'\' AND InviatoA =\''.$inviatoA.'\' AND CompilatoDa =\''.$dottoreID.'\'';
            $result = mysql_query($query) or die(mysql_error());

            while ($record=mysql_fetch_assoc($result)) $id = $record['IDTerapia'];

            echo json_encode(array(
                 'status' => 'standard',
                 'data' => $id));

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
  case "aggiungiFarmaco":
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
            $IDTerapia = mysql_real_escape_string(strip_tags($_REQUEST['data']['IDTerapia']));
            $IDFarmaco = mysql_real_escape_string(strip_tags($_REQUEST['data']['IDFarmaco']));

            /* Preparo ed eseguo la query */
            $query = 'INSERT INTO Prescrizioni (IDTerapia, IDFarmaco) VALUES (\''.$IDTerapia.'\', \''.$IDFarmaco.'\')';
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
  case "aggiornaDettagliAssunzioneFarmaco":
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
            $IDTerapia = mysql_real_escape_string(strip_tags($_REQUEST['data']['IDTerapia']));
            $IDFarmaco = mysql_real_escape_string(strip_tags($_REQUEST['data']['IDFarmaco']));
            $Dosaggio = mysql_real_escape_string(strip_tags($_REQUEST['data']['Dosaggio']));
            $DataInizio = mysql_real_escape_string(strip_tags($_REQUEST['data']['DataInizio']));
            $DataTermine = mysql_real_escape_string(strip_tags($_REQUEST['data']['DataTermine']));
            $OraInizio = mysql_real_escape_string(strip_tags($_REQUEST['data']['OraInizio']));
            $Intervallo = mysql_real_escape_string(strip_tags($_REQUEST['data']['Intervallo']));
            $Lunedi = mysql_real_escape_string(strip_tags($_REQUEST['data']['Lunedi']));
            $Martedi = mysql_real_escape_string(strip_tags($_REQUEST['data']['Martedi']));
            $Mercoledi = mysql_real_escape_string(strip_tags($_REQUEST['data']['Mercoledi']));
            $Giovedi = mysql_real_escape_string(strip_tags($_REQUEST['data']['Giovedi']));
            $Venerdi = mysql_real_escape_string(strip_tags($_REQUEST['data']['Venerdi']));
            $Sabato = mysql_real_escape_string(strip_tags($_REQUEST['data']['Sabato']));
            $Domenica = mysql_real_escape_string(strip_tags($_REQUEST['data']['Domenica']));

            /* Preparo ed eseguo la query */
            $query = 'UPDATE Prescrizioni
                      SET Dosaggio = \''.$Dosaggio.'\' ,
                      DataTermine = \''.$DataTermine.'\' ,
                      DataInizio = \''.$DataInizio.'\' ,
                      OraInizio = \''.$OraInizio.'\' ,
                      Intervallo = \''.$Intervallo.'\' ,
                      Lunedi = \''.$Lunedi.'\' ,
                      Martedi = \''.$Martedi.'\' ,
                      Mercoledi = \''.$Mercoledi.'\' ,
                      Giovedi = \''.$Giovedi.'\' ,
                      Venerdi = \''.$Venerdi.'\'  ,
                      Sabato = \''.$Sabato.'\'  ,
                      Domenica = \''.$Domenica.'\'
                      WHERE IDTerapia=\''.$IDTerapia.'\' AND IDFarmaco=\''.$IDFarmaco.'\'';
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
}

?>
