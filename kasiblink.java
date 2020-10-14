// *** RELAY ISO PARA OMNICHANNEL/SIBLINK ***
// V1.stable
//
//********************************************

import java.io.*;
import java.net.*;
import java.util.*;

class kasiblink {
  public static int kainterval = 9000;
  public static int SLport = 4000;
  public static int SLECOresptimeout = 3000;

  public static void main(String args[]) throws Exception {
    final BufferedInputStream din;
    final BufferedOutputStream dout;
    final Socket s;

    // CREA LA CONEXION A SIBLINK
    //    s = new Socket(InetAddress.getByName("172.30.85.72"), SLport);
    try {
      s = new Socket(InetAddress.getByName("localhost"), SLport);
      din = new BufferedInputStream(s.getInputStream(), 2048);
      dout = new BufferedOutputStream(s.getOutputStream(), 2048);

      // COMIENZA EL ENVIO DE ECOS
      while (true) {
        try {
          Thread.sleep(4000);
          EnvioEco(s, din, dout);
        } catch (InterruptedException e) {
          EnvioEco(s, din, dout);
        }
      }
    } catch (Exception ee) {
      System.out.println("Error de conexion: " + ee);
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
    String str4 =
      "ISO0140000130200B23A800128E0961E000000001600011AF31000000000000139mmddhhmmss015457hhmmssmmddmmddmmdd11           3799990386986532000=7412               000555      009900054       00000000000    00000000000000000000000000000000000000AR032FFFFFFFFFFFFFFFF023                       24764XJ8G7V95GWXD9EMPYR0Z          FAC0386272951964703860001003000046011133001004601113       0386230589027643860036205000040056918036004005691       4058960001082013   CASTRO, EMILCE EVANGELINA               NND0                                     012LINKLNK1+0000130386LNK11100P02554                       016                11614        28001004601113                28                            033000000000000000000000000000000000001 001 0430000000000000000000000000000000000000000000";
    int i, j, l;

    //      Escribo ECO .....
    try {
      i = str4.length();
      for (j = 0; j < i; j++) b[j] = (byte) str4.charAt(j);
      dout.write(i / 256);
      dout.write(i % 256);
      dout.write(b, 0, i);
      dout.flush();
      System.out.println("Msg:" + str4);
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
        //        din.close();
        //        dout.close();
        //        s.close();
        return 2;
      } catch (Exception ee) {
        System.out.println("No pude cerrar el socket ");
        return 2;
      }
    }

    return 0;
  }
} // Fin de Class kasiblink
