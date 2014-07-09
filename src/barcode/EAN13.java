package barcode;

public class EAN13 {
    /// 获取 EAN-13 条形码
    public static String Get(String s, int width, int height)
    {
      int sum_even = 0;//偶数位之和
      int sum_odd = 0;//奇数位之和

      for (int i = 0; i < 12; i++)
      {
        if (i % 2 == 0)
        {
          sum_odd += Character.getNumericValue(s.charAt(i));
        }
        else
        {
          sum_even += Character.getNumericValue(s.charAt(i));
        }
      }

      //校验码
      int checkcode = (10 - (sum_even * 3 + sum_odd) % 10) % 10;
      //变成13位
      s += checkcode;

      // 00000000000101左侧42个01010右侧35个校验7个1010000000

      String resultBin = "";//二进制串
      //起始符
      resultBin += "00000000000101";

      String type = ean13type(s.charAt(0));
      //左侧数据符
      for (int i = 1; i < 7; i++)
      {
        resultBin += ean13(s.charAt(i), type.charAt(i - 1));
      }
      //中间分隔符
      resultBin += "01010";
      //右侧数据符
      for (int i = 7; i < 13; i++)
      {
        resultBin += ean13(s.charAt(i), 'C');//右侧数据符及校验符均用字符集中的C子集表示
      }
      //终止符
      resultBin += "1010000000";
      return resultBin;
    }
    private static String ean13(char c, char type)
    {
      switch (type)
      {
        case 'A':
          {
            switch (c)
            {
              case '0': return "0001101";
              case '1': return "0011001";
              case '2': return "0010011";
              case '3': return "0111101";//011101
              case '4': return "0100011";
              case '5': return "0110001";
              case '6': return "0101111";
              case '7': return "0111011";
              case '8': return "0110111";
              case '9': return "0001011";
              default: return "Error!";
            }
          }
        case 'B':
          {
            switch (c)
            {
              case '0': return "0100111";
              case '1': return "0110011";
              case '2': return "0011011";
              case '3': return "0100001";
              case '4': return "0011101";
              case '5': return "0111001";
              case '6': return "0000101";//000101
              case '7': return "0010001";
              case '8': return "0001001";
              case '9': return "0010111";
              default: return "Error!";
            }
          }
        case 'C':
          {
            switch (c)
            {
              case '0': return "1110010";
              case '1': return "1100110";
              case '2': return "1101100";
              case '3': return "1000010";
              case '4': return "1011100";
              case '5': return "1001110";
              case '6': return "1010000";
              case '7': return "1000100";
              case '8': return "1001000";
              case '9': return "1110100";
              default: return "Error!";
            }
          }
        default: return "Error!";
      }
    }
    private static String ean13type(char c)
    {
      switch (c)
      {
        case '0': return "AAAAAA";
        case '1': return "AABABB";
        case '2': return "AABBAB";
        case '3': return "AABBBA";
        case '4': return "ABAABB";
        case '5': return "ABBAAB";
        case '6': return "ABBBAA";//中国
        case '7': return "ABABAB";
        case '8': return "ABABBA";
        case '9': return "ABBABA";
        default: return "Error!";
      }
    }
}
