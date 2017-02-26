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
  case "ricercaFarmaco":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((isset($_COOKIE['UserID'])) && (($_COOKIE['Type'] == "Paziente") || ($_COOKIE['Type'] == "Dottore")))
    {
       try
       {

        $manager = new FarmacoManager();
        echo $manager->ricercaFarmaco($_REQUEST['data']['Keyword']);
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
  case "creaFarmaco":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((isset($_COOKIE['UserID'])) && ($_COOKIE['Type'] == "Dottore"))
    {
       try
       {
         $manager = new FarmacoManager();
         echo $manager->creaFarmaco($_REQUEST['data']['Nome'], $_REQUEST['data']['Composizione']);
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
  case "esistenzaFarmaco":
  {
    /* Verifico se l'utente è autorizzato a utilizzare questa operazione */
    if((isset($_COOKIE['UserID'])) && (($_COOKIE['Type'] == "Paziente") || ($_COOKIE['Type'] == "Dottore")))
    {
       try
       {
         $manager = new FarmacoManager();
         echo $manager->esistenzaFarmaco($_REQUEST['data']['Nome'], $_REQUEST['data']['Composizione']);
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
}


Class FarmacoManager{

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

  public function ricercaFarmaco($keyword)
  {
    /* Parsing dei dati  per evitare la MYSQL Injection */
    $keyword = mysql_real_escape_string(strip_tags($keyword));

    /* Preparo ed eseguo la query */
    $query = 'SELECT * FROM Farmaci WHERE Nome Like \''.$keyword.'%\'';
    $result = mysql_query($query) or die(mysql_error());

    //Se la chiave di ricerca non corrisponde a nessun dottore...
    if(mysql_num_rows($result) == 0)
    {
      return json_encode(array(
           'status' => 'standard',
           'data' => array(
             'count' => 0
           )));
    }else{

      $i = -1;
      while ($record=mysql_fetch_assoc($result)){

          $i++;
          $data[$i] = array(
            'IDFarmaco' => $record['IDFarmaco'],
            'Nome' => $record['Nome'],
            'Composizione' => $record['Composizione']);
      }

      return json_encode(array(
           'status' => 'standard',
           'data' => array(
             'count' => mysql_num_rows($result),
             'lista_farmaci' => $data
           )));
    }

  }

  public function creaFarmaco($nome, $composizione)
  {
    /* Parsing dei dati  per evitare la MYSQL Injection */
    $nome = mysql_real_escape_string(strip_tags($nome));
    $composizione = mysql_real_escape_string(strip_tags($composizione));

    /* Preparo ed eseguo la query */
    $query = 'INSERT INTO Farmaci (Nome, Composizione) VALUES (\''.$nome.'\', \''.$composizione.'\')';
    mysql_query($query) or die(mysql_error());

    $query = 'SELECT IDFarmaco FROM Farmaci WHERE Nome=\''.$nome.'\' AND Composizione =\''.$composizione.'\'';
    $result = mysql_query($query) or die(mysql_error());

    while ($record=mysql_fetch_assoc($result)) $id = $record['IDFarmaco'];

    return json_encode(array(
         'status' => 'standard',
         'data' => $id));
  }

  public function esistenzaFarmaco($nome, $composizione)
  {
    /* Parsing dei dati  per evitare la MYSQL Injection */
    $nome = mysql_real_escape_string(strip_tags($nome));
    $composizione = mysql_real_escape_string(strip_tags($composizione));

    /* Preparo ed eseguo la query */
    $query ='SELECT *
            FROM Farmaci
            WHERE Nome=\''.$nome.'\' and Composizione=\''.$composizione.'\' ';

    $result = mysql_query($query) or die(mysql_error());

    //Se la chiave di ricerca non corrisponde a nessun dottore...
    if(mysql_num_rows($result) == 0)
    {
      return json_encode(array(
           'status' => 'standard',
           'data' => array(
             'count' => 0
           )));
    }else{

      $i = -1;
      while ($record=mysql_fetch_assoc($result)){

          $i++;
          $data[$i] = array(
            'IDFarmaco' => $record['IDFarmaco'],
            'Nome' => $record['Nome'],
            'Composizione' => $record['Composizione']);
      }

      return json_encode(array(
           'status' => 'standard',
           'data' => array(
             'count' => mysql_num_rows($result),
             'lista_farmaci' => $data
           )));
    }
  }
}
?>
