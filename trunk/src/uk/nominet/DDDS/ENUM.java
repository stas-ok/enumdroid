package uk.nominet.DDDS;

import org.xbill.DNS.NAPTRRecord;

public class ENUM extends DDDSinDNS {

	String suffix = null;

	public ENUM(String suffix) {
		this.suffix = suffix;
	}

	public ENUM() {
		this("e164.arpa");
	}

	protected String convertToAUS(String input) {
		char[] ca = input.toCharArray();
		int len = ca.length;

		StringBuffer sb = new StringBuffer(len);
		for (int i = 0; i < len; ++i) {
			char c = ca[i];
			if (c == '+') {
				sb.append(c);
			} else if (Character.isDigit(c)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	protected String convertToDatabaseKey(String key) {

		char[] ca = key.toCharArray();
		int len = ca.length;

		StringBuffer sb = new StringBuffer(len * 2);
		for (int i = len - 1; i > 0; --i) {
			sb.append(ca[i]);
			sb.append('.');
		}
		sb.append(suffix);

		return sb.toString();
	}

	protected Rule createRule(String key, NAPTRRecord record) {
		return new EnumRule(key, record);
	}

	public class EnumRule extends DNSRule {

		public EnumRule(String key, NAPTRRecord record) {
			super(key, record);
		}

		public boolean isTerminal() {
			return flags.toLowerCase().equals("u");
		}

	}
}
