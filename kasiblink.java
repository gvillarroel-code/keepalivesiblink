// *** GENERADOR DE TRX ECOS PARA OMNICHANNEL/SIBLINK ***
// V1.stable
//
//********************************************

import java.io.*;
import java.net.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

class kasiblink {
  public static int kainterval = 9000;
  public static int SLport = 4000;
  public static int SLECOresptimeout = 3000;
  public static Calendar x;
  public static String fecha;
  public static String tramafinalmmdd;
  public static String tramafinalhhmmss;
  public static Format f;
  public static boolean flagloopeco;
  public static boolean flagmain;
  public static int ecostatus;
  public static BufferedInputStream din;
  public static BufferedOutputStream dout;
  public static Socket s;


  public static void main(String args[]) throws Exception {

    System.out.println(
        "Inicio demorado 60 seg"
    );

    Thread.sleep(60000);

    x = Calendar.getInstance();
    kasiblink.fecha =
      Integer.toString(x.get(x.MONTH) + 101).substring(1, 3) +
      Integer.toString(x.get(x.DATE) + 100).substring(1, 3);

    if (System.getenv("RELAYIP") == null) {
      System.out.println(
        "Por favor defina la variable de amiente RELAYIP (export RELAYIP='xx.xx.xx.xx')"
      );
      flagmain = false;
    } else {
      flagmain = true;
    }

    // CREA LA CONEXION A SIBLINK
    //    s = new Socket(InetAddress.getByName("172.30.85.72"), SLport);
    while (flagmain) {
      System.out.println(
        "Iniciando KeepAlive SibLink/SFB en:" + System.getenv("RELAYIP")
      );
      try {
        s = new Socket(InetAddress.getByName(System.getenv("RELAYIP")), SLport);
        din = new BufferedInputStream(s.getInputStream(), 2048);
        dout = new BufferedOutputStream(s.getOutputStream(), 2048);

        // COMIENZA EL ENVIO DE ECOS
        flagloopeco = true;
        while (flagloopeco) {
          try {
            Thread.sleep(4000);

            if (EnvioEco(s, din, dout) != 0) {
              flagloopeco = false;
            }
          } catch (InterruptedException e) {
            if (EnvioEco(s, din, dout) != 0) {
              flagloopeco = false;
            }
          }
        }
      } catch (Exception ee) {
        System.out.println(
          "Error de conexion al puerto: " +
          SLport +
          ", posiblemente el relayISO este inactivo (cerrando conexio y reintentando....) "
        );
        Thread.sleep(5000);
        din.close();
        dout.close();
        s.close();
      }
    }
  } // Fin de Main

  // FUNCION QUE GENERA ECO PARA MANTENER VIVA LA CONEXION
  //
  public static int EnvioEco(
    Socket s,
    BufferedInputStream din,
    BufferedOutputStream dout
  ) {
    byte b[] = new byte[8192];
    String str = "", str2 =
      "ISO0040000400800822000000000000004000000000000001234561234567890301";
    String str5 =
      "ISO0140000130200B23A800128E0961E000000001600011AF31000000000000139mmddhhmmss015457hhmmssmmddmmddmmdd11           3799990386986532000=7412               000555      009900054       00000000000    00000000000000000000000000000000000000AR032FFFFFFFFFFFFFFFF023                       24764XJ8G7V95GWXD9EMPYR0Z          FAC0386272951964703860001003000046011133001004601113       0386230589027643860036205000040056918036004005691       4058960001082013   CASTRO, EMILCE EVANGELINA               NND0                                     012LINKLNK1+0000130386LNK11100P02554                       016                11614        28001004601113                28                            033000000000000000000000000000000000001 001 0430000000000000000000000000000000000000000000";

    String str3 =
      "ISO0140000100200B23A800128E0941E000000001600011A092000000000070000mmddhhmmss000001hhmmssmmddmmddmmdd11((acq.ins))37<track3.............................>123456789012tes1tes1tes1tes1term.term.term.locationlocationlocationlocationlocation032pin.pin.pin.pin.023*adddataadddataadddata*0120150********0130386TES1*****00588xxx016pinopinopinopino11recinstcode28184004002634++++++++++++++++28............................033termaddrtermaddrtermaddrtermaddr.001.001.043,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,";

    String str4 =
      "ISO0140000100220B23A800128E0941A000000001600011AA50000000000000000mmddhhmmss000002hhmmssmmddmmddmmdd11((acq.ins))37<track3.............................>123456789012tes1tes1tes1tes1term.term.term.locationlocationlocationlocationlocation032pin.pin.pin.pin.023N27030076821ataadddata*012**term.dat**013*card issuer*016pinopinopinopino11recinstcode28052004664928++++++++++++++++28............................033termaddrtermaddrtermaddrtermaddr.001.001.043,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,";
    
    // NUEVA TRAMA WSECO - EN TEST
    String str6 =
      "WSECOT00538912NOV202011048204801@0000";
    
    int i, j, l;

    kasiblink.f = new SimpleDateFormat("HHmmss");
    kasiblink.tramafinalhhmmss = f.format(new Date());
    //System.out.println("Time = "+kasiblink.strhhmmss);

    kasiblink.tramafinalmmdd = str6.replaceAll("mmdd", fecha);
    kasiblink.tramafinalhhmmss =
    kasiblink.tramafinalmmdd.replaceAll("hhmmss", kasiblink.tramafinalhhmmss);

    //    System.out.println(str6);
    //    System.out.println("###############################################");
    //    System.out.println(kasiblink.tramafinalhhmmss);

    str6 = kasiblink.tramafinalhhmmss;

    //      Escribo ECO .....

    try {
      i = str6.length();
      for (j = 0; j < i; j++) b[j] = (byte) str6.charAt(j);
      dout.write(i / 256);
      dout.write(i % 256);
      dout.write(b, 0, i);
      dout.flush();
      //      System.out.println("Msg:" + str6);
    } catch (Exception e) {
      System.out.println("No Pude enviar ECO SFB  Cerrando conexion ... ");
      try {
        din.close();
        dout.close();
        s.close();
        return 2;
      } catch (Exception ee) {
        System.out.println("No pude cerrar el socket");
        return 2;
      }
    }

    //      Espero Respuesta del ECO .....
    try {
      s.setSoTimeout(kasiblink.SLECOresptimeout);
      i = din.read();
      j = din.read();
      din.read(b, 0, i * 256 + j);
      str = "";
      for (l = 0; l < (i * 256 + j); l++) str = str + (char) b[l];
      //      System.out.println(
      //        "Respuesta ISO ECO SFB Recibida.. "
      //      );
    } catch (Exception e) {
      System.out.println("Respuesta ECO SFB no recibida.. Cerrando conexion  ");
      try {
        din.close();
        dout.close();
        s.close();
        return 2;
      } catch (Exception ee) {
        System.out.println("No pude cerrar el socket ");
        return 2;
      }
    }

    return 0;
  }
} // Fin de Class kasiblink
