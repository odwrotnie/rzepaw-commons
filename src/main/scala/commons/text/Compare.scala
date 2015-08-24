package commons.text

import org.apache.commons.lang.StringUtils

object Compare extends StringUtils {

	/**
	 * TODO
	 *
	 * This is the number of changes needed to change one String into another,
	 * where each change is a single character modification (deletion, insertion or substitution).
	 *
	 * Trzeba wziąć pod uwagę to, że w krótszych napisach potrzeba mniej zmian.
	 */
	def apply(s1: String, s2: String) = StringUtils.getLevenshteinDistance(s1, s2)
}
