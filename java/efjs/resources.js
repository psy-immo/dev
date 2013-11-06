/**
 * resources.js, (c) 2013, Immanuel Albrecht; Dresden University of Technology,
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

// In this file, we define common (possibly language dependent) resources for the user interface

res = {};

res_US = {};
res_DE = {};


res["US"] = res_US;
res["DE"] = res_DE;

res_US["inferenceRectified"] = "Your solution has been corrected, unnecessary points have been removed and missing points are lit orange.";
res_DE["inferenceRectified"] = "Deine Lösung wurde korrigiert. Unnötige Aussagen wurden entfernt und fehlende ergänzt und orange gekennzeichnet.";

res_US["inferenceCorrect"] = "Congratulations! Your solution is correct and complete!";
res_DE["inferenceCorrect"] = "Sehr gut! Deine Lösung ist vollständig und richtig!";

res_US["inferenceErrors"] = "Your solution contains some errors.";
res_DE["inferenceErrors"] = "Deine Lösung enthält noch Fehler.";

res_US["inferenceJustify"] = "Your solution needs some more justifying points.";
res_DE["inferenceJustify"] = "Deiner Lösung fehlen noch Argumente zur Begründung.";

res_US["inferenceIncomplete"] = "Some parts of the solution are still missing.";
res_DE["inferenceIncomplete"] = "Es fehlt noch ein Teil der Lösung.";


/**
 * default to US English as resource language
 */
if (!language)
{
	language = "US";
}

function getRes(which) {
	return res[language][which];
}