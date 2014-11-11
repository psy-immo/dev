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
myInferenceMachineNames = {};

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

function InferenceMachine(name,atags, rtags, stringids, hypergraph, points,
		conclusions) {
	this.id = myInferenceMachineCount++;

	if (name) {
		this.name = name;
	} else {
		this.name = "";
		for ( var i = 0; i < atags.length; ++i) {
			this.name += atags[i];
		}
		this.name += "_" + this.id;
	}
	
	/**
	 * the assertion domain
	 */
	this.stringids = stringids;

	/**
	 * ids that should be justified
	 */
	this.justify = [];
	
	/**
	 * done?
	 */
	this.done = false;

	/**
	 * ids that are given implicitly
	 */
	this.implicit = [];
	
	/**
	 * contains lists of names for autospan objects,
	 * that are shown/hidden after rectification respectively
	 */
	this.hideAutoSpanAfterRect = [];
	this.showAutoSpanAfterRect = [];

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
	 * whether to lock the airport after the rectification
	 */
	
	this.lockAfterRectification = true;

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
	 * adds autospans to hide after rectification
	 * 
	 * @param namelist     a string with , separated names for autospan objects
	 * 
	 * @returns this
	 */
	
	this.HideAfterRectification = function(namelist) {
		var split = namelist.split(",");
		for ( var int = 0; int < split.length; int++) {
			var x = split[int];
			this.hideAutoSpanAfterRect.push(x);
		}
		
		
		return this;
	};
	
	/**
	 * adds autospans to show after rectification
	 * 
	 * @param namelist     a string with , separated names for autospan objects
	 * 
	 * @returns this
	 */
	
	this.ShowAfterRectification = function(namelist) {
		var split = namelist.split(",");
		for ( var int = 0; int < split.length; int++) {
			var x = split[int];
			this.showAutoSpanAfterRect.push(x);
		}
		
		
		return this;
	};

	/**
	 * whether to lock the airport after the rectification of the solution
	 */

	this.LockAfterRectification = function(doLock) {
		this.lockAfterRectification = doLock;

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
		
		var unjustified_point_count =  closed_points.unjustified.length;

		for ( var int2 = 0; int2 < closed_points.unjustified.length; int2++) {
			var point_id = closed_points.unjustified[int2];
			// log_data += "DEBUG: "+closed_points.unjustified[int2]+"\n";
			var s = this.stringids.FromId(point_id);
			// log_data += "DEBUG: "+int2+"\n";

			log_data += s + " [" + point_id + "] ";
			
			if (! (assertions[point_id]))
				unjustified_point_count -= 1;

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
						if (need_justification.indexOf(id) >= 0)
							p.MarkAsUnjustified();
						else
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

		var some_todos_left = false;

		for ( var int6 = 0; int6 < this.justify.length; int6++) {
			var id = this.justify[int6];
			if (closed_points.justified.indexOf(id) < 0) {
				has_been_solved = false;
				log_data += "\n Justification-TO-DO not solved: "
						+ this.stringids.FromId(id) + " [" + id + "]";
				some_todos_left = true;
			}
		}
		
		if (some_todos_left) {
			if (feedback_info)
				feedback_info += "<br/>";
			feedback_info += getRes("inferenceMissingArguments");
		}

		/**
		 * log solve-status
		 */

		if (has_been_solved) {
			log_data += "\nAll tasks have been SOLVED.\n";
			feedback_info = getRes("inferenceCorrect");
			this.solved = 1;
		} else {

			if (incorrect_point_count > 0) {
				if (feedback_info)
					feedback_info += "<br/>";
				feedback_info += getRes("inferenceErrors");
			}
			
			if (unjustified_point_count > 0) {
				if (feedback_info)
					feedback_info += "<br/>";
				feedback_info += getRes("inferenceJustify");
			}
		}

		/**
		 * probably
		 */

		if (this.rectify) {
			var do_rectification = has_been_solved;
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
				
				if (this.lockAfterRectification)
					airport.Lock(true);
				
				this.done = true;
				
				/**
				 * magically show some elements
				 */
				
				for ( var int99 = 0; int99 < this.showAutoSpanAfterRect.length; int99++) {
					var name = this.showAutoSpanAfterRect[int99];
					
					var span = autoSpanNames[name];
					
					if (span)
					{
						span.SetValue(1);
					}
					
				}
				
				/**
				 * magically hide some elements
				 */
				
				for ( var int99 = 0; int99 < this.hideAutoSpanAfterRect.length; int99++) {
					var name = this.hideAutoSpanAfterRect[int99];
					
					var span = autoSpanNames[name];
					
					if (span)
					{
						span.SetValue(0);
					}
					
				}
				
				
				this.rectifications += 1;
				
				if (has_been_solved)
					feedback_info = getRes("inferenceRectifiedAfterSolved");
				else
					feedback_info = getRes("inferenceRectified");
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
		return "" + this.tryNumber + ";" + this.solved+";"+this.rectifications+";"+(this.done?1:0);
	};

	/**
	 * restore the given state
	 */

	this.SetValue = function(contents) {
		var parts = contents.split(";");
		this.tryNumber = parseInt(parts[0]);
		this.solved = parseInt(parts[1]);
		this.rectifications = parseInt(parts[2]);
		this.done = parseInt(parts[3])!=0;
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
	
	myInferenceMachineNames[this.name] = this;

	return this;
}


/**
 * REMOVED OLD VERSION (not supported anymore)
 */