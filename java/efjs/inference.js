/**
 * inference.js, (c) 2012-13, Immanuel Albrecht; Dresden University of
 * Technology, Professur für die Psychologie des Lernen und Lehrens
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

myInferenceMachines = [];
myInferenceMachineCount = 0;

/**
 * Creates a new inference machine
 * 
 * @param atags
 *            tags for input acceptance
 * @param rtags
 *            tags for input rejection
 * @param stringids
 *            StringIds object that represents the assertion domain
 * @param hypergraph
 *            InferenceGraph object that represents all basic inference rules
 *            wrt. the assertion domain
 * @param points
 *            additional tag for input acceptance as *points* (default:
 *            "points")
 * @param conclusions
 *            additional tag for input acceptance as *conclusions* (default:
 *            "conclusions")
 * @returns this
 */

function InferenceMachine(atags, rtags, stringids, hypergraph, points,
		conclusions) {
	this.id = myInferenceMachineCount++;

	/**
	 * the assertion domain
	 */
	this.stringids = stringids;

	/**
	 * ids that should be justified
	 */
	this.justify = [];

	/**
	 * ids that are given implicitly
	 */
	this.implicit = [];

	/**
	 * the inference hyper graph
	 */
	this.hypergraph = hypergraph;

	/**
	 * the hint feedback data
	 */

	this.hints = {};
	
	/**
	 * store the rectification count
	 */
	
	this.rectifications = 0;

	/**
	 * store the number of tries
	 */

	this.tryNumber = 1;

	/**
	 * store the name of the FeedbackDisplay object, or false if no textual/html
	 * feedback is given
	 */

	this.feedback = false;

	/**
	 * store the solution status
	 * 
	 */

	this.solved = 0;

	/**
	 * store the correct solution check(s)
	 */

	this.solutionRequirements = [];

	this.acceptTags = atags;
	this.rejectTags = rtags;

	this.name = "";
	for ( var i = 0; i < atags.length; ++i) {
		this.name += atags[i];
	}
	this.name += "_" + this.id;

	if (typeof points == "undefined") {
		this.points = "points";
	} else {
		this.points = points;
	}

	if (typeof conclusions == "undefined") {
		this.conclusions = "conclusions";
	} else {
		this.conclusions = conclusions;
	}

	/**
	 * wait for these other actions to trigger/complete before allowing using
	 * the check answer button
	 */

	this.waitfor = [];

	/**
	 * button label
	 */
	this.text = "Check your answer";

	/**
	 * store the name of the airport that is going to be rectified
	 */

	this.rectify = false;

	/**
	 * store the name of the timer that triggers rectification
	 */

	this.rectifyTimer = false;

	/**
	 * store the count-down-counter that triggers rectification
	 */

	this.rectifyCounter = false;

	/**
	 * write all needed html elements to the document
	 */

	this.WriteHtml = function() {
		document.write(this.GetHtml());
	};

	/**
	 * add a new requirement for solution checking
	 */

	this.Requirement = function(fn) {

		this.solutionRequirements.push(fn);

		return this;
	};

	/**
	 * set the name of the airport that is rectified
	 */

	this.Rectify = function(airport) {
		this.rectify = airport;

		return this;
	};

	/**
	 * set the name of the feedback target
	 */

	this.Feedback = function(target_display) {
		this.feedback = target_display;

		return this;
	};

	/**
	 * set the name of the timer that triggers rectification
	 */

	this.RectifyTimer = function(timer) {
		this.rectifyTimer = timer;

		return this;
	};

	/**
	 * set the name of the counter that triggers rectification
	 */

	this.RectifyCounter = function(counter) {
		this.rectifyCounter = counter;

		return this;
	};

	/**
	 * @param ids
	 *            list of ids that should be justified
	 * 
	 * @returns this
	 */

	this.JustifyToDo = function(ids) {

		for ( var int = 0; int < ids.length; int++) {
			var id = ids[int];
			this.justify.push(id);
		}

		this.justify.sort();

		return this;
	};

	/**
	 * @param ids
	 *            list of ids that should be considered to be given implicitly
	 * 
	 * @returns this
	 */

	this.ImplicitPoints = function(ids) {

		for ( var int = 0; int < ids.length; int++) {
			var id = ids[int];
			this.implicit.push(id);
		}

		this.implicit.sort();

		return this;
	};

	/**
	 * get the html code for this inference machine button
	 */

	this.GetHtml = function() {
		var html = "";

		html += "<form onsubmit=\"return false;\" class=\"inference\">";
		html += "<input class=\"inference\" type=\"button\" value=\""
				+ this.text + "\" onclick=\"myInferenceMachines[" + this.id
				+ "].StartMachine()\"/>";
		html += "</form>";

		return html;
	};

	/**
	 * add the appropriate hint data
	 */

	this.AddHint = function(lack, hint) {
		this.hints[lack] = hint;

		return this;
	};

	/**
	 * this handler is called when the Check-Answer button has been pressed
	 */

	this.StartMachine = function(slave_call) {
		var feedback_info = "";

		/**
		 * check, whether giving a solution is allowed
		 */
		for ( var int = 0; int < this.waitfor.length; int++) {
			if (this.waitfor[int]() != true) {
				myLogger.Log("myInferenceMachines[" + this.id + "] ("
						+ this.name + ") start: check refused by " + int + ".");
				return;
			}
		}

		/**
		 * fetch all inputs
		 */

		this.acceptTags.push(this.points);

		var points = myTags.AllTagsBut(this.acceptTags, this.rejectTags);

		this.acceptTags.pop();

		this.acceptTags.push(this.conclusions);

		var conclusions = myTags.AllTagsBut(this.acceptTags, this.rejectTags);

		this.acceptTags.pop();

		/**
		 * here we save the ids of all given assertions
		 */

		var assertions = {};

		var log_data = this.name + " check answer triggered.\n";
		log_data += "Try: " + this.tryNumber + "\n";
		this.tryNumber += 1;

		var incorrect_point_count = 0;

		/**
		 * store which parts of the problem have been solved correctly
		 */

		var correctly_solved_parts = [];

		log_data += "\nPoints:\n";

		for ( var int = 0; int < points.length; ++int) {
			var point = points[int].token;
			var point_id = this.stringids.ToId(point);
			log_data += point + " [" + point_id + "] ";

			if (this.hypergraph.IsCorrect(point_id)) {
				log_data += "[correct] ";

				var solves_parts = this.hypergraph.SolvesWhichParts(point_id);
				for ( var ints2 = 0; ints2 < solves_parts.length; ints2++) {
					var part = solves_parts[ints2];

					log_data += "[[" + part + "]] ";

					if (correctly_solved_parts.indexOf(part) < 0) {
						correctly_solved_parts.push(part);
					}
				}

			} else {
				incorrect_point_count += 1;
			}

			if (this.hypergraph.IsTrivial(point_id)) {
				log_data += "[trivial] ";
			}

			if (this.hypergraph.IsConcluding(point_id)) {
				log_data += "[concluding] ";
			}

			if (this.hypergraph.IsJustified(point_id)) {
				log_data += "[justified] ";
			}

			log_data += "\n";

			assertions[point_id] = true;

		}

		log_data += "Conlusions:\n";

		for ( var int = 0; int < conclusions.length; ++int) {
			var point = conclusions[int].token;
			var point_id = this.stringids.ToId(point);
			log_data += point + " [" + point_id + "] ";

			if (this.hypergraph.IsCorrect(point_id)) {
				log_data += "[correct] ";
				var solves_parts = this.hypergraph.SolvesWhichParts(point_id);
				for ( var ints2 = 0; ints2 < solves_parts.length; ints2++) {
					var part = solves_parts[ints2];

					log_data += "[[" + part + "]] ";

					if (correctly_solved_parts.indexOf(part) < 0) {
						correctly_solved_parts.push(part);
					}
				}
			} else {
				incorrect_point_count += 1;
			}

			if (this.hypergraph.IsTrivial(point_id)) {
				log_data += "[trivial] ";
			}

			if (this.hypergraph.IsConcluding(point_id)) {
				log_data += "[concluding] ";
			}

			if (this.hypergraph.IsJustified(point_id)) {
				log_data += "[justified] ";
			}

			log_data += "\n";

			assertions[point_id] = true;
		}

		log_data += "Implicit Points:\n";

		for ( var int = 0; int < this.implicit.length; ++int) {

			var point_id = this.implicit[int];
			var point = this.stringids.FromId(point_id);
			log_data += point + " [" + point_id + "] ";

			if (this.hypergraph.IsCorrect(point_id)) {
				log_data += "[correct] ";
				var solves_parts = this.hypergraph.SolvesWhichParts(point_id);
				for ( var ints2 = 0; ints2 < solves_parts.length; ints2++) {
					var part = solves_parts[ints2];

					log_data += "[[" + part + "]] ";

					if (correctly_solved_parts.indexOf(part) < 0) {
						correctly_solved_parts.push(part);
					}
				}
			}

			if (this.hypergraph.IsTrivial(point_id)) {
				log_data += "[trivial] ";
			}

			if (this.hypergraph.IsConcluding(point_id)) {
				log_data += "[concluding] ";
			}

			if (this.hypergraph.IsJustified(point_id)) {
				log_data += "[justified] ";
			}

			log_data += "\n";

			assertions[point_id] = true;
		}
		log_data += "\n";

		log_data += "Justification TO-DOs:\n";

		for ( var int = 0; int < this.justify.length; ++int) {

			var point_id = this.justify[int];
			var point = this.stringids.FromId(point_id);
			log_data += point + " [" + point_id + "] ";

			if (this.hypergraph.IsCorrect(point_id)) {
				log_data += "[correct] ";
				var solves_parts = this.hypergraph.SolvesWhichParts(point_id);
				for ( var ints2 = 0; ints2 < solves_parts.length; ints2++) {
					var part = solves_parts[ints2];

					log_data += "[[" + part + "]] ";

					if (correctly_solved_parts.indexOf(part) < 0) {
						correctly_solved_parts.push(part);
					}
				}
			}

			if (this.hypergraph.IsTrivial(point_id)) {
				log_data += "[trivial] ";
			}

			if (this.hypergraph.IsConcluding(point_id)) {
				log_data += "[concluding] ";
			}

			if (this.hypergraph.IsJustified(point_id)) {
				log_data += "[justified] ";
			}

			log_data += "\n";
		}

		/**
		 * first, we calculate whether all points given are justified
		 */

		var closed_points = this.hypergraph.CloseJustification(Object
				.keys(assertions), this.justify);

		// log_data += "DEBUG: "+closed_points.justified+"\n";
		log_data += "\nJustified Points:\n";

		for ( var int2 = 0; int2 < closed_points.justified.length; int2++) {
			var point_id = closed_points.justified[int2];
			var s = this.stringids.FromId(point_id);

			log_data += s + " [" + point_id + "] ";

			if (this.hypergraph.IsCorrect(point_id)) {
				log_data += "[correct] ";

				var solves_parts = this.hypergraph.SolvesWhichParts(point_id);
				for ( var ints2 = 0; ints2 < solves_parts.length; ints2++) {
					var part = solves_parts[ints2];

					log_data += "[[" + part + "]] ";
				}
			}

			if (this.hypergraph.IsTrivial(point_id)) {
				log_data += "[trivial] ";
			}

			if (this.hypergraph.IsConcluding(point_id)) {
				log_data += "[concluding] ";
			}

			if (this.hypergraph.IsJustified(point_id)) {
				log_data += "[justified] ";
			}

			log_data += "\n";
		}

		log_data += "\nUnjustified Points:\n";
		// log_data += "DEBUG: "+closed_points.unjustified+"\n";

		for ( var int2 = 0; int2 < closed_points.unjustified.length; int2++) {
			var point_id = closed_points.unjustified[int2];
			// log_data += "DEBUG: "+closed_points.unjustified[int2]+"\n";
			var s = this.stringids.FromId(point_id);
			// log_data += "DEBUG: "+int2+"\n";

			log_data += s + " [" + point_id + "] ";

			if (this.hypergraph.IsCorrect(point_id)) {
				log_data += "[correct] ";

				var solves_parts = this.hypergraph.SolvesWhichParts(point_id);
				for ( var ints2 = 0; ints2 < solves_parts.length; ints2++) {
					var part = solves_parts[ints2];

					log_data += "[[" + part + "]] ";
				}
			}

			if (this.hypergraph.IsTrivial(point_id)) {
				log_data += "[trivial] ";
			}

			if (this.hypergraph.IsConcluding(point_id)) {
				log_data += "[concluding] ";
			}

			if (this.hypergraph.IsJustified(point_id)) {
				log_data += "[justified] ";
			}

			log_data += "\n";
		}

		/**
		 * now we check for a small set of points that need to be justified such
		 * that the given points would have been all justified if those points
		 * were present
		 */

		var need_justification = this.hypergraph
				.WhichPointsNeedJustification(closed_points);

		log_data += "\nJustification Hint:\n";

		for ( var int2 = 0; int2 < need_justification.length; int2++) {
			var point_id = need_justification[int2];
			var s = this.stringids.FromId(point_id);

			log_data += s + " [" + point_id + "] ";

			if (this.hypergraph.IsCorrect(point_id)) {
				log_data += "[correct] ";

				var solves_parts = this.hypergraph.SolvesWhichParts(point_id);
				for ( var ints2 = 0; ints2 < solves_parts.length; ints2++) {
					var part = solves_parts[ints2];

					log_data += "[[" + part + "]] ";
				}
			}

			if (this.hypergraph.IsTrivial(point_id)) {
				log_data += "[trivial] ";
			}

			if (this.hypergraph.IsConcluding(point_id)) {
				log_data += "[concluding] ";
			}

			if (this.hypergraph.IsJustified(point_id)) {
				log_data += "[justified] ";
			}

			log_data += "\n";

		}

		/**
		 * close justified points and need_justification points
		 */

		var additional_points = this.hypergraph.GetAdditionalAssertions(
				closed_points.justified, need_justification);

		var hints_for_parts = [];

		log_data += "\nPoints Missing:\n";

		for ( var intpts = 0; intpts < additional_points.length; intpts++) {
			var point_id = additional_points[intpts];

			var s = this.stringids.FromId(point_id);

			log_data += s + " [" + point_id + "] ";

			if (this.hypergraph.IsCorrect(point_id)) {
				log_data += "[correct] ";

				var solves_parts = this.hypergraph
						.IndicatesWhichLacks(point_id);
				for ( var ints2 = 0; ints2 < solves_parts.length; ints2++) {
					var part = solves_parts[ints2];

					if (hints_for_parts.indexOf(part) < 0) {
						hints_for_parts.push(part);
					}

					log_data += "[(" + part + ")] ";
				}
			}

			if (this.hypergraph.IsTrivial(point_id)) {
				log_data += "[trivial] ";
			}

			if (this.hypergraph.IsConcluding(point_id)) {
				log_data += "[concluding] ";
			}

			if (this.hypergraph.IsJustified(point_id)) {
				log_data += "[justified] ";
			}

			log_data += "\n";

		}

		/**
		 * check both assertions plus missing points, which are necessary
		 */

		var check_for = closed_points.justified;

		if (this.justify)
			check_for = this.justify;

		var necessary = this.hypergraph.GetNecessarySubset(Object
				.keys(assertions), this.implicit, additional_points, check_for);

		var hint_points = [];

		log_data += "\nNecessary Augmented Points:\n";

		for ( var intpts = 0; intpts < necessary.length; intpts++) {
			var point_id = necessary[intpts];

			var s = this.stringids.FromId(point_id);

			if (this.implicit.indexOf(point_id) >= 0) {
				log_data += "i ";
			} else if (assertions[point_id])
				log_data += "X ";
			else {
				log_data += "  ";

				hint_points.push(point_id);
			}

			log_data += s + " [" + point_id + "] ";

			if (this.hypergraph.IsCorrect(point_id)) {
				log_data += "[correct] ";

				var solves_parts = this.hypergraph
						.IndicatesWhichLacks(point_id);
				for ( var ints2 = 0; ints2 < solves_parts.length; ints2++) {
					var part = solves_parts[ints2];

					if (hints_for_parts.indexOf(part) < 0) {
						hints_for_parts.push(part);
					}

					log_data += "[(" + part + ")] ";
				}
			}

			if (this.hypergraph.IsTrivial(point_id)) {
				log_data += "[trivial] ";
			}

			if (this.hypergraph.IsConcluding(point_id)) {
				log_data += "[concluding] ";
			}

			if (this.hypergraph.IsJustified(point_id)) {
				log_data += "[justified] ";
			}

			log_data += "\n";
		}

		/**
		 * change indication colors for given points
		 */

		var sources = [ points, conclusions ];

		for ( var int9 = 0; int9 < sources.length; int9++) {
			var points = sources[int9];

			for ( var int3 = 0; int3 < points.length; int3++) {
				var p = points[int3];
				var id = this.stringids.ToId(p.token);
				if (this.hypergraph.IsTrivial(id)) {
					p.MarkAsTrivial();
				} else if (this.hypergraph.IsCorrect(id) == false) {
					p.MarkAsBad();
				} else {
					if (necessary.indexOf(id) >= 0) {
						if (need_justification.indexOf(id) >= 0)
							p.MarkAsUnjustified();
						else
							p.MarkAsGood();
					} else {
						p.MarkAsUnnecessary();
					}
				}
			}
		}

		/**
		 * check the solution criteria
		 */

		correctly_solved_parts.sort();
		hints_for_parts.sort();

		log_data += "\nCorrect Solution Parts: ";

		for ( var int4 = 0; int4 < correctly_solved_parts.length; int4++) {
			var part = correctly_solved_parts[int4];
			if (int4 > 0)
				log_data += ", ";
			log_data += part;
		}

		log_data += "\nPossible Hints: ";

		for ( var int4 = 0; int4 < hints_for_parts.length; int4++) {
			var part = hints_for_parts[int4];
			if (int4 > 0)
				log_data += ", ";
			log_data += part;
		}

		var requirements_met = 0;
		var requirements_unmet = 0;

		for ( var int5 = 0; int5 < this.solutionRequirements.length; int5++) {
			var fn = this.solutionRequirements[int5];

			if (fn(correctly_solved_parts)) {
				log_data += "\nCriterion " + (1 + int5) + " MET.";
				requirements_met += 1;
			} else {
				log_data += "\nCriterion " + (1 + int5) + " NOT met.";
				requirements_unmet += 1;
			}

		}

		var need_some_justification = additional_points.length > 0;

		var has_been_solved = (requirements_unmet == 0)
				&& (incorrect_point_count == 0) && (!need_some_justification);

		/**
		 * We check whether the justification to-do's have been met
		 */

		// TODO: Hack in a good feedback thing-ie
		for ( var int6 = 0; int6 < this.justify.length; int6++) {
			var id = this.justify[int6];
			if (closed_points.justified.indexOf(id) < 0) {
				has_been_solved = false;
				log_data += "\n Justification-TO-DO not solved: "
						+ this.stringids.FromId(id) + " [" + id + "]";
			}
		}

		/**
		 * log solve-status
		 */

		if (has_been_solved) {
			log_data += "\nAll tasks have been SOLVED.\n";
			feedback = getRes("inferenceCorrect");
			this.solved = 1;
		} else {

			if (incorrect_point_count > 0) {
				feedback += getRes("inferenceErrors") + "<br/>";
			}
		}

		/**
		 * probably
		 */

		if (this.rectify) {
			var do_rectification = false;
			if (this.rectifyTimer) {
				if (timerNames[this.rectifyTimer].value) {
					log_data += "\nRectification timer has not elapsed.";

				} else
					do_rectification = true;
			}
			
			if (this.rectifyCounter) {
				/**
				 * reduce the number of tries, if this is not a slave call
				 */
				if (!(slave_call))
					counterNames[this.rectifyCounter].SetValue(counterNames[this.rectifyCounter].value-1);
				
				if (counterNames[this.rectifyCounter].value) {
					log_data +="\nRectification counter is non-zero.";
				} else
					do_rectification = true;
			}

			if (do_rectification) {
				var airport = airportNames[this.rectify];
				log_data += "\nRectifying points.";

				var keep = [];
				var add = [];

				for ( var int7 = 0; int7 < necessary.length; int7++) {
					var id = necessary[int7];
					var text = this.stringids.FromId(id);

					keep.push(text);

					if (this.implicit.indexOf(id) < 0) {
						add.push(text);
					}
				}
				airport.Rectify(keep, add);
				
				this.rectifications += 1;
			}

		}

		/**
		 * put feedback to display
		 */

		if (this.feedback) {
			var display = feedbackNames[this.feedback];

			display.SetValue(feedback_info);

			log_data += "\nFeedback Display:\n" + feedback_info;
		} else
			log_data += "\nNo feedback displayed.\n";

		/**
		 * log the results
		 */

		myLogger.Log(log_data);

	};

	/**
	 * return the current state
	 */
	this.GetValue = function() {
		return "" + this.tryNumber + ";" + this.solved+";"+this.rectifications;
	};

	/**
	 * restore the given state
	 */

	this.SetValue = function(contents) {
		var parts = contents.split(";");
		this.tryNumber = parseInt(parts[0]);
		this.solved = parseInt(parts[1]);
		this.rectifications = parseInt(parts[2]);
	};

	/**
	 * this function sets the text of the component
	 * 
	 * @returns this
	 */
	this.Text = function(text) {
		this.text = text;
		return this;
	};

	/**
	 * this function sets the name of the component
	 * 
	 * @returns this
	 */
	this.Name = function(name) {
		this.name = name;
		return this;
	};

	/**
	 * this function adds a waitfor-function of the component, which is called
	 * whenever the answer button is clicked, and which has to return true in
	 * order to check the answer. The given function is responsible to alert the
	 * user that the answer will not be checked.
	 * 
	 * @returns this
	 */

	this.WaitFor = function(waitforfn) {
		this.waitfor[this.waitfor.length] = waitforfn;

		return this;
	};

	myStorage.RegisterField(this, "myInferenceMachines[" + this.id + "]");
	myInferenceMachines[this.id] = this;

	return this;
}

/*******************************************************************************
 * **** OLD version
 * 
 * 
 ******************************************************************************/

myInferenceButtons = [];
myInferenceId = 0;

/**
 * generate a check inference button
 * 
 * (this is the old version of the inference machine using a java applet)
 * 
 * @param atags
 *            accept tags
 * @param rtags
 *            reject tags
 * @param points
 *            points tag
 * @param conclusions
 *            conclusion tag
 * 
 * @returns
 */

function InferenceButton(atags, rtags, points, conclusions) {

	this.id = myInferenceId++;
	this.errorCount = 0;

	this.acceptTags = atags;
	this.rejectTags = rtags;

	this.name = "";
	for ( var i = 0; i < atags.length; ++i) {
		this.name += atags[i];
	}
	this.name += "_" + this.id;

	this.food = [];

	if (typeof points == "undefined") {
		this.points = "points";
	} else {
		this.points = points;
	}

	if (typeof conclusions == "undefined") {
		this.conclusions = "conclusions";
	} else {
		this.conclusions = conclusions;
	}

	/**
	 * unlike the answer button, the feedback is given by the applet
	 */
	this.currentFeedback = "";
	this.text = "Check your answer";

	this.incompleteSolution = "Your solution is not complete! <br/>";
	this.injustifiedSolution = "The yellow sentences need to be further justified! <br/>";
	this.correctSolution = "Correct!";
	this.incorrectSolution = "The red points/conclusions are not correct.<br/>";
	this.lackHints = {};
	this.requiredParts = [];
	this.requiredCount = 0;

	this.waitfor = [];

	this.WriteHtml = function() {

		document
				.write("<applet id=\"inferenceApplet"
						+ this.id
						+ "\" "
						+ "name=\"inferenceApplet"
						+ this.id
						+ "\""
						+ " archive=\"inferenceApplet.jar\" "
						+ "code=\"de.tu_dresden.psy.inference.regexp.xml.InferenceMachine\" "
						+ "MAYSCRIPT style=\"width: 1px; height: 1px\"></applet>");

		document.write("<form onsubmit=\"return false;\">");
		document.write("<input type=\"button\" value=\"" + this.text
				+ "\" onclick=\"myInferenceButtons[" + this.id
				+ "].OnClick()\"/>");
		document
				.write("<br /><table border=0 cellpadding=0 cellspacing=0><tr><td id=\"InferenceHint"
						+ this.id
						+ "\" style=\"height: 20px; width: 100%\"></td></tr></table>");
		document.write("</form>");
	};

	this.AddHint = function(lack, hint) {
		this.lackHints[lack] = hint;

		return this;
	};

	this.RequirePart = function(part) {
		this.requiredParts.push(part.toLowerCase());

		return this;
	};

	this.RequireCount = function(count) {

		this.requiredCount = count;

		return this;
	};

	this.SetCorrect = function(text) {
		this.correctSolution = text;

		return this;
	};

	this.SetIncorrect = function(text) {
		this.incorrectSolution = text;

		return this;
	};

	this.SetIncomplete = function(text) {
		this.incompleteSolution = text;

		return this;
	};

	this.SetInjustified = function(text) {
		this.injustifiedSolution = text;

		return this;
	};

	/**
	 * add data that is post-poned to be fed into the applet after the page
	 * loaded.
	 * 
	 * @returns this
	 */

	this.Feed = function(encodedData) {
		for ( var int = 0; int < encodedData.length; ++int) {
			this.food.push(encodedData[int]);
		}
		return this;
	};

	/**
	 * We have to wait for the page to finish loading the applet instance before
	 * we may feed it with the relevant information
	 */
	this.FeedApplet = function() {
		var applet = document.getElementById("inferenceApplet" + this.id);

		if (this.food.length) {
			try {
				var ret = applet.feed(bugfixParam(decodeString(this.food)));

				myLogger.Log(this.name + " feed: " + ret);
			} catch (err) {
				myLogger.Log(this.name + " feed error: " + err);
			}
		}
		;
	};

	/**
	 * this function sets the text of the component
	 * 
	 * @returns this
	 */
	this.Text = function(text) {
		this.text = text;
		return this;
	};

	/**
	 * this function sets the name of the component
	 * 
	 * @returns this
	 */
	this.Name = function(name) {
		this.name = name;
		return this;
	};

	/**
	 * this function adds a waitfor-function of the component, which is called
	 * whenever the answer button is clicked, and which has to return true in
	 * order to check the answer. The given function is responsible to alert the
	 * user that the answer will not be checked.
	 * 
	 * @returns this
	 */

	this.WaitFor = function(waitforfn) {
		this.waitfor[this.waitfor.length] = waitforfn;

		return this;
	};

	/**
	 * this is called whenever the button is clicked
	 */
	this.OnClick = function() {

		var unjustified = false;

		/**
		 * check, whether giving a solution is allowed
		 */
		for ( var int = 0; int < this.waitfor.length; int++) {
			if (this.waitfor[int]() != true) {
				myLogger.Log("Check answer " + this.id + ": check refused by "
						+ int + ".");
				return;
			}
		}

		var applet = document.getElementById("inferenceApplet" + this.id);

		/**
		 * reset student's state
		 */

		try {

			applet.resetStudentsState();
		} catch (err) {
			myLogger.Log("Check answer " + this.id + ": reset failed " + err);
			return;
		}

		/**
		 * fetch all inputs
		 */

		this.acceptTags.push(this.points);

		var points = myTags.AllTagsBut(this.acceptTags, this.rejectTags);

		this.acceptTags.pop();

		this.acceptTags.push(this.conclusions);

		var conclusions = myTags.AllTagsBut(this.acceptTags, this.rejectTags);

		this.acceptTags.pop();

		/**
		 * submit inputs
		 */

		var log_data = this.name + " check answer triggered.\nPoints:\n";

		for ( var int = 0; int < points.length; ++int) {
			log_data += points[int].token + "\n";
			try {
				applet.addPoint(bugfixParam("" + points[int].token));
			} catch (err) {
				myLogger.Log("Check answer " + this.id + ": addPoint failed "
						+ err);
				return;
			}
			points[int].MarkNeutral();
		}

		log_data += "Conlusions:\n";

		for ( var int = 0; int < conclusions.length; ++int) {
			log_data += conclusions[int].token + "\n";
			try {
				applet.addConclusion(bugfixParam("" + conclusions[int].token));
			} catch (err) {
				myLogger.Log("Check answer " + this.id
						+ ": addConclusion failed " + err);
				return;
			}
			conclusions[int].MarkNeutral();
		}

		log_data += "Result:\n";

		var result = "";
		try {
			result = applet.checkAnswerAndFeedback();
		} catch (err) {
			myLogger.Log("Check answer " + this.id
					+ ": checkAnswerAndFeedback failed " + err);
			return;
		}
		log_data += result;

		/**
		 * decode feedback result
		 * 
		 */

		var lines = result.split("\n");

		var status = lines[0].split(",");

		/**
		 * get correct points
		 */

		var int = 2;
		var count = parseInt(lines[1]);

		var correct_points = {};

		while (count > 0) {
			--count;

			correct_points[lines[int]] = 1;
			++int;
		}

		/**
		 * get wrong points
		 */

		count = parseInt(lines[int]);
		int++;

		var wrong_points = {};

		while (count > 0) {
			--count;

			wrong_points[lines[int]] = 1;
			++int;
		}

		/**
		 * get unjustified points
		 */

		count = parseInt(lines[int]);
		int++;

		var unjustified_points = {};

		while (count > 0) {
			--count;

			unjustified_points[lines[int]] = 1;
			++int;

			unjustified = true;
		}

		/**
		 * get correct conclusions
		 */

		count = parseInt(lines[int]);
		int++;

		var correct_conclusions = {};

		while (count > 0) {
			--count;

			correct_conclusions[lines[int]] = 1;
			++int;
		}

		/**
		 * get good conclusions
		 */

		count = parseInt(lines[int]);
		int++;

		var good_conclusions = {};

		while (count > 0) {
			--count;

			good_conclusions[lines[int]] = 1;
			++int;
		}

		/**
		 * get wrong conclusions
		 */

		count = parseInt(lines[int]);
		int++;

		var wrong_conclusions = {};

		while (count > 0) {
			--count;

			wrong_conclusions[lines[int]] = 1;
			++int;
		}

		/**
		 * get unjustified conclusions
		 */

		count = parseInt(lines[int]);
		int++;

		var unjustified_conclusions = {};

		while (count > 0) {
			--count;

			unjustified_conclusions[lines[int]] = 1;
			++int;

			unjustified = true;
		}

		/**
		 * colorize points
		 */

		for ( var int = 0; int < points.length; ++int) {
			if (("" + points[int].token) in unjustified_points) {
				points[int].MarkAsOkay();
			} else if (("" + points[int].token) in correct_points) {
				points[int].MarkAsGood();
			} else if (("" + points[int].token) in wrong_points) {
				points[int].MarkAsBad();
			}

		}

		/**
		 * colorize conclusions
		 */

		for ( var int = 0; int < conclusions.length; ++int) {
			if (("" + conclusions[int].token) in unjustified_conclusions) {
				conclusions[int].MarkAsOkay();
			} else if (("" + conclusions[int].token) in good_conclusions) {
				conclusions[int].MarkAsGood();
			} else if (("" + conclusions[int].token) in wrong_conclusions) {
				conclusions[int].MarkAsBad();
			}
		}

		/**
		 * use status to give additional feedback
		 */

		this.currentFeedback = "";

		var incorrect = (status[0].trim() != "") || (status[1].trim() != "");

		if (incorrect) {
			this.currentFeedback += this.incorrectSolution;
		}

		if (unjustified) {
			this.currentFeedback += this.injustifiedSolution;
		}

		var lacks = status[2].trim().substr(6).split("&");

		for ( var int = 0; int < lacks.length; int++) {
			if (lacks[int] in this.lackHints) {
				this.currentFeedback += decodeString(this.lackHints[lacks[int]]);
			}
		}

		var parts = status[3].trim().substr(6).split("&");

		var incomplete = parts.length < this.requiredCount;

		for ( var int = 0; int < this.requiredParts.length; int++) {
			if (parts.lastIndexOf(this.requiredParts[int]) < 0)
				incomplete = true;
		}

		if (incomplete) {
			this.currentFeedback += this.incompleteSolution;
		}

		if ((!incomplete) && (!unjustified) && (!incorrect)) {
			this.currentFeedback += this.correctSolution;
		}

		this.SetHint(this.currentFeedback);

		myLogger.Log(log_data);

	};

	/**
	 * this function sets the contents of the hint area
	 */
	this.SetHint = function(contents) {
		var td = document.getElementById("InferenceHint" + this.id);
		td.innerHTML = contents;
	};

	/**
	 * return the current state
	 */
	this.GetValue = function() {
		return "" + this.errorCount + "\n" + escapeBTNR(this.currentFeedback);
	};

	/**
	 * restore the given state
	 */

	this.SetValue = function(contents) {
		var data = ("" + contents).split("\n");
		if (data.length == 2) {
			this.errorCount = parseInt(data[0]);

			this.currentFeedback = unescapeBTNR(data[1]);

			this.SetHint(this.currentFeedback);

		}
	};

	myStorage.RegisterField(this, "myInferenceButton[" + this.id + "]");
	myInferenceButtons[this.id] = this;

	return this;
};

/**
 * this function takes care of giving the loaded applets the information they
 * need to operate
 */

function FeedInferenceApplets() {
	for ( var int = 0; int < myInferenceButtons.length; int++) {
		var btn = myInferenceButtons[int];
		btn.FeedApplet();
	}
}
