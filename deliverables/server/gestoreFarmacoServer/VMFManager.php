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
  case "visualizzaVMF":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if(isset($_COOKIE['UserID']) && (($_COOKIE['Type'] == "Dottore") || ($_COOKIE['Type'] == "Paziente")))
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
            $query = 'SELECT Farmaci.Nome , Farmaci.Composizione , VMF.Scadenza , VMF.IDFarmaco
                      FROM VMF, Farmaci WHERE VMF.IDFarmaco=Farmaci.IDFarmaco AND VMF.UserID = \''.$userID.'\'';
            $result = mysql_query($query) or die(mysql_error());

            //Se l'utente non ha alcun farmaco nel proprio vmf...
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
                    'Nome' => $record['Nome'],
                    'Composizione' => $record['Composizione'],
                    'Scadenza' => $record['Scadenza'],
                    'IDFarmaco' => $record['IDFarmaco']);
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
  case "aggiungiFarmacoVMF":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((isset($_COOKIE['UserID'])) && (($_COOKIE['Type'] == "Dottore") || ($_COOKIE['Type'] == "Paziente")))
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
            $IDFarmaco = mysql_real_escape_string(strip_tags($_REQUEST['data']['IDFarmaco']));
            $scadenza = mysql_real_escape_string(strip_tags($_REQUEST['data']['Scadenza']));

            /* Preparo ed eseguo la query */
            $query = 'INSERT INTO VMF (UserID, IDFarmaco, Scadenza) VALUES (\''.$userID.'\', \''.$IDFarmaco.'\', \''.$scadenza.'\')';
            $result = mysql_query($query) or die(mysql_error());

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
  case "eliminaFarmacoVMF":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((isset($_COOKIE['UserID'])) && (($_COOKIE['Type'] == "Paziente") || ($_COOKIE['Type'] == "Dottore")))
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
            $IDFarmaco = mysql_real_escape_string(strip_tags($_REQUEST['data']['IDFarmaco']));

            /* Preparo ed eseguo la query */
            $query = 'DELETE FROM VMF WHERE UserID = \''.$userID.'\' AND IDFarmaco = \''.$IDFarmaco.'\'';
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
  case "elencoFarmaciScaduti":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if(isset($_COOKIE['UserID']) && (($_COOKIE['Type'] == "Dottore") || ($_COOKIE['Type'] == "Paziente")))
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
            $query = 'SELECT Farmaci.Nome,VMF.Scadenza,Farmaci.Composizione
            FROM Farmaci,VMF
            WHERE Farmaci.IDFarmaco=VMF.IDFarmaco and VMF.UserID = \''.$userID.'\'
            AND Scadenza <= \''.$_REQUEST['data']['Anno'].'/'.$_REQUEST['data']['Mese'].'/'.$_REQUEST['data']['Giorno'].'\'
            ORDER BY Scadenza DESC';

            $result = mysql_query($query) or die(mysql_error());

            //Se l'utente non ha farmaci scaduti...
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
                    'Nome' => $record['Nome'],
                    'Composizione'=>$record['Composizione'],
                    'Scadenza' => $record['Scadenza']);
              }

              echo json_encode(array(
                   'status' => 'standard',
                   'data' => array(
                     'count' => mysql_num_rows($result),
                     'lista_scadenze' => $data
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
  case "elencoFarmaciInScadenza":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if(isset($_COOKIE['UserID']) && (($_COOKIE['Type'] == "Dottore") || ($_COOKIE['Type'] == "Paziente")))
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
            $query = 'SELECT Farmaci.Nome, Farmaci.IDFarmaco, VMF.Scadenza,Farmaci.Composizione
            FROM Farmaci,VMF
            WHERE Farmaci.IDFarmaco=VMF.IDFarmaco and VMF.UserID = \''.$userID.'\'
            AND Scadenza > \''.$_REQUEST['data']['Anno'].'/'.$_REQUEST['data']['Mese'].'/'.$_REQUEST['data']['Giorno'].'\'
            ORDER BY Scadenza DESC';

            $result = mysql_query($query) or die(mysql_error());

            //Se l'utente non ha farmaci scaduti...
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
                    'Nome' => $record['Nome'],
                    'IDFarmaco' => $record['IDFarmaco'],
                    'Composizione'=>$record['Composizione'],
                    'Scadenza' => $record['Scadenza']);
              }

              echo json_encode(array(
                   'status' => 'standard',
                   'data' => array(
                     'count' => mysql_num_rows($result),
                     'lista_scadenze' => $data
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

?>
