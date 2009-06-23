package uk.nominet.DDDS;

import java.util.List;

public abstract class DDDS {

	protected String aus = null;

	protected abstract String convertToAUS(String key);
	protected abstract String convertToDatabaseKey(String key);
	protected abstract Rule[] lookupRules(String key);

	/*
	 * "Identity"
	 */
	protected String firstWellKnownRule(String key) {
		return key;
	}

	protected void doLookup(String key, List<Rule> rules) {
		Rule[] aRules = lookupRules(key);

		if (aRules == null) {
			return;
		}

		for (Rule rule: aRules) {
			if (rule.isTerminal()) {
				rules.add(rule);
			} else {
				/* recurse if necessary */
				doLookup(rule.getResult(), rules);
			}
		}
	}

	public void lookup(String input, List<Rule> rules) {
		aus = convertToAUS(input);
		String key = firstWellKnownRule(aus);
		key = convertToDatabaseKey(key);
		doLookup(key, rules);
	}
}
