<?php
include_once 'control_users.php';

if (isset($_REQUEST["id_base"])) {

    $json_resp = array();
    
    $ref = $_REQUEST["id_base"];
    
    $new_nom = $_REQUEST["nom"];
    $new_ape = $_REQUEST["ape"];
    $new_id = $_REQUEST["id"];
    $new_tipo_id = $_REQUEST["tipo_id"];
    $new_tel = $_REQUEST["tel"];
    $new_mail = $_REQUEST["email"];
    $new_nick = $_REQUEST["nick"];
    $new_pass = $_REQUEST["pass"];
    $new_rol = $_REQUEST["rol"];

    $usuarios_mng = new ControlUsuario();

    $json_resp["modif"] = NULL;
    
    $query = "update usuarios set id='$new_id', nombre='$new_nom', apes='$new_ape', tipo_id='$new_tipo_id',"
            . " telefono='$new_tel', email='$new_mail', rol='$new_rol'";
    
    //$buildQuery = "";
    
    if ($new_pass != NULL || (strlen($new_pass) > 0)) {
        $query = $query . ", pass='$new_pass'";
    }
    
    if ($new_nick != NULL || (strlen($new_nick) > 0)) {
        $query = $query . ", nick='$new_nick'";
    }
    
    $query = $query." where id='$ref'";
    
    $resp_bd = $usuarios_mng->modificarUsuario($query);
    
    if ($resp_bd === "modificado") {
        $json_resp["modif"] = TRUE;
        $json_resp["error"] = NULL;
    }
    else {
        $json_resp["modif"] = FALSE;
        $json_resp["error"] = $resp_bd;
    }

    header('Content-type: application/json; charset=utf-8');
    echo json_encode($json_resp);
    
exit();
}
?>
<!--<!DOCTYPE html><html>
    <head>
        <style>
            *{
                border: 1px dashed black;
            }
        </style>
    </head>
    <body>
        <p id="display1"></p>
        <br>
        <p id="display2"></p>
        <br><br>
        <form action="modif_user.php" method="post">
            <label>Ref</label>
            <input type="text" name="ref"/><br>
            <label>Nombre</label>
            <input type="text" name="nom"/><br>
            <label>Apellido</label>
            <input type="text" name="ape"/><br>
            <label>Id</label>
            <input type="text" name="id"/><br>
            <label>Tipo Id</label>
            <input type="text" name="tipo_id"/><br>
            <label>Telefono</label>
            <input type="text" name="tel"/><br>
            <label>Email</label>
            <input type="text" name="email"/><br>
            <label>Nick</label>
            <input type="text" name="nick"/><br>
            <label>Password</label>
            <input type="text" name="pass"/><br>
            <label>Rol</label>
            <input type="text" name="rol"/><br>
            <input type="submit" value="Submit"/>
        </form>
    </body>
</html>-->