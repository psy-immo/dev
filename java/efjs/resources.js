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

res_US["inferenceMissingArguments"] = "Your solution is still incomplete.";
res_DE["inferenceMissingArguments"] = "Deine Lösung ist noch nicht vollständig.";

res_US["answerErrors"] = "Your solution still contains an error. Correct parts of your solution are lit green.";
res_DE["answerErrors"] = "Deine Lösung enthält noch Fehler. Richtige Lösungsfragmente wurden grün gekennzeichnet.";

res_US["answerCorrect"] =  "Correct!";
res_DE["answerCorrect"] = "Richtig!";

res_US["answerButton"] = "Check your answer";
res_DE["answerButton"] = "Antwort überprüfen";

res_US["remoteButtonText"] = "Check your answer";
res_DE["remoteButtonText"] = "Antwort überprüfen";

res_US["inferenceRectifiedAfterSolved"] = "Correct! Points that are not necessary for the solution may have been removed.";
res_DE["inferenceRectifiedAfterSolved"] = "Richtig! Gegebenenfalls wurden unnötige Argumente aus deiner Lösung entfernt.";


res_US["inferenceRectified"] = "Your solution has been corrected. Both incorrect and unnecessary points have been removed and missing points are lit orange.";
res_DE["inferenceRectified"] = "Deine Lösung wurde korrigiert. Sowohl unrichtige als auch unnötige Aussagen wurden entfernt, sowie fehlende ergänzt und orange gekennzeichnet.";

res_US["inferenceCorrect"] = "Congratulations! Your solution is correct and complete!";
res_DE["inferenceCorrect"] = "Sehr gut! Deine Lösung ist vollständig und richtig!";

res_US["inferenceErrors"] = "Your solution contains some errors.";
res_DE["inferenceErrors"] = "Deine Lösung enthält noch Fehler.";

res_US["inferenceJustify"] = "Your solution needs some more justifying points.";
res_DE["inferenceJustify"] = "Einige Argumente benötigen noch weitere Begründung.";

res_US["inferenceIncomplete"] = "Some parts of the solution are still missing.";
res_DE["inferenceIncomplete"] = "Es fehlt noch ein Teil der Lösung.";


/**
 * default to US English as resource language
 */
if (typeof language == "undefined")
{
	language = "US";
}

function getRes(which) {
	return res[language][which];
}