package uk.nominet.DDDS;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.NAPTRRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.Type;

public abstract class DDDSinDNS extends DDDS {

	public Rule[] lookupRules(String key) {

		try {
			Resolver resolver = new ExtendedResolver();
			resolver.setTimeout(2);
			Lookup lookup = new Lookup(key, Type.NAPTR);
			lookup.setResolver(resolver);
			Record[] records = lookup.run();
			if (records == null) {
				return null;
			}

			Rule[] rules = new Rule[records.length];
			for (int i = 0; i < records.length; ++i) {
				// type check necessary in case of other RRtypes in the Answer Section
				if (records[i] instanceof NAPTRRecord) {
					rules[i] = createRule(key, (NAPTRRecord)records[i]);
				}
			}

			Arrays.sort(rules);

			return rules;
		} catch (Exception e) {
			return null;
		}
	}

	protected abstract Rule createRule(String key, NAPTRRecord record);

	public abstract class DNSRule implements Rule, Comparable<DNSRule> {

		protected String key;
		protected int order;
		protected int preference;
		protected String flags;
		protected String service;
		protected String regexp;
		protected Name replacement;

		public int compareTo(DNSRule r) {
			if (order < r.order) {
				return -1;
			} else if (order > r.order) {
				return +1;
			} else {
				if (preference < r.preference) {
					return -1;
				} else if (preference > r.preference) {
					return +1;
				} else {
					return 0;
				}
			}
		}

		public int getOrder() {
			return order;
		}

		public int getPriority() {
			return preference;
		}

		public String getFlags() {
			return flags;
		}

		public String getService() {
			return service;
		}

		public DNSRule(String key, NAPTRRecord record) {
			this.key = key;

			order = record.getOrder();
			preference = record.getPreference();
			flags = unescape(record.getFlags());
			service = unescape(record.getService());
			regexp = unescape(record.getRegexp());
			replacement = record.getReplacement();
		}

		private String unescape(String input) {
			char[] c = input.toCharArray();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < c.length; ++i) {
				if (c[i] == '\\') {
					// @todo - may fall off the end
					i++;
				}
				sb.append(c[i]);
			}
			return sb.toString();
		}

		public String getResult() {

			String find = null;
			String replace = null;

			if (regexp == null || regexp.length() <= 0) {
				return replacement.toString();
			}

			char[] c = regexp.toCharArray();
			char delim = c[0];

			// @todo - more sanity checking
			int i, j;
			for (i = 1; i < c.length; i++) {
				if (c[i] == '\\') continue;
				if (c[i] == delim) {
					find = regexp.substring(1, i++);
					break;
				}
			}

			for (j = i ; j < c.length; ++j) {
				if (c[j] == '\\') continue;
				if (c[j] == delim) {
					replace = regexp.substring(i, j);
					break;
				}
			}

			// failed to parse - crap out here
			if (find == null || replace == null) {
				return null;
			}

			// convert \digit to $digit
			replace = replace.replaceAll("\\\\(\\d)", "\\$$1");
			
			// @todo - support case insensitive flag
			Pattern p = Pattern.compile(find);
			Matcher m = p.matcher(aus);
			if (m.matches()) {
				return m.replaceFirst(replace);
			} else {
				return null;
			}
		}

		public String toString() {
			return service + " " + getResult();
		}
		
		public abstract boolean isTerminal();
	}
}
