/**
 * efmlfactory.js, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
 * Professur für die Psychologie des Lernen und Lehrens
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * creates a new efml tag object according to the description string,
 * tags, accept and reject are given to propagate
 */

function NewEfmlTag(description, name, tags, accept, reject) {
	var trimmed = description.trimLeft();
	if (trimmed.startsWith("EfmlQuote ")) {
		return new EfmlQuote(unescapeBTNR(trimmed.substr(10)),tags);
	}
	if (trimmed.startsWith("EfmlBoard ")) {
		var block = new EfmlBlock(name,tags,accept,reject,true);
		return block.SetContents(unescapeBTNR(trimmed.substr(10)));
	}
	
	return new EfmlQuote(description,tags);
};
