package com.dlz.comm.util.encry;

/**
 * 多进制转换
 * @author dingkui
 *
 */
public class ParseUtil {
	public enum ParserEnum{
		D627("ZmXgakWAwbFnvPMsNiDrGYpdTRVCy3jzL7H2ulh45BKOQ0IxS1t9efJqo6Uc8E",7,3),
		D623("0Ix2fJqMsN5SrGQ3jzXgakWTRVCyAwY8Eho6L7Hpdi1t9ebO4BKDuZmFnvPlUc",3,3),
		D523("YuaEilJnTzBKVQedwAbtjOWZUIpFrxXShoLsfqycDkvCGmRPgMHN",3,4),
		D367("VYCT9ODEJQR5XILKGWH3PASZ06M12U47FB8N",7,4),
		D3675("VYCT9ODEJQR5XILKGWH3PASZ06M12U47FB8N",7,5),
		D1014("5930468271",1,4);
		private char[] rDigits;
		private int jm=7;//跳跃因子防止篡改
		private long tl=0; //补齐到多少位
		ParserEnum(String dict,int jm,int tl){
			this.rDigits=dict.toCharArray();
			this.jm=jm;
			String a="";
			for(int i=0;i<tl;i++){
				if(i==0){
					a+=rDigits[1];
				}else{
					a+=rDigits[0];
				}
			}
			try {
				this.tl=ParseUtil.decode(a, rDigits);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public String encode(long value){
			return ParseUtil.encode(value*jm+tl,rDigits);
		}
		public Long decode(String value){
			long r=-1;
			try {
				r = ParseUtil.decode(value,rDigits)-tl;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return r%jm==0?r/jm:-1l;
		}
	}
	
	/**
	 * long转多进制
	 * @param value
	 * @return
	 */
	public static String encode(long value,char[] rDigits) {
		int digitIndex = 0;
		int radix = rDigits.length;
		long longPositive = Math.abs(value);
		char[] outDigits = new char[20];
		for (digitIndex = 0; digitIndex <20; digitIndex++) {
			if (longPositive == 0) {
				break;
			}
			outDigits[outDigits.length - digitIndex - 1] = rDigits[Long.valueOf(longPositive % radix).intValue()];
			longPositive /= radix;
		}
		return new String(outDigits, outDigits.length - digitIndex, digitIndex);
	}

	/**
	 * 多进制转long
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static long decode(String value,char[] rDigits) throws Exception {
		int fromBase = rDigits.length;
		if (value == null || value.trim().equals("")) {
			return -1L;
		}
		value=value.trim().replaceAll("[^A-Za-z0-9]", "");
		char[] values = value.toCharArray();

		String sDigits = new String(rDigits, 0, fromBase);
		long result = 0;
		for (int i = 0; i < value.length(); i++) {
			if (!sDigits.contains(String.valueOf(values[i]))) {
				return -1l;
			} else {
				try {
					int index = 0;
					for (int xx = 0; xx < rDigits.length; xx++) {
						if (rDigits[xx] == values[value.length() - i - 1]) {
							index = xx;
							break;
						}
					}
					result += Math.pow(fromBase, i) * index;// 2
				} catch (Exception e) {
					
					throw new Exception("运算溢出",e);
				}
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
//		System.out.println(ParserEnum.D523.encode(1025));
//		System.out.println(ParserEnum.D523.encode(2025));
		System.out.println(ParserEnum.D3675.encode(8089999));
	}
}