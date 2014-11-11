#!/usr/bin/python3
# coding: utf-8
#
# userinterfaces.py, (c) 2014, Immanuel Albrecht; Professur für Lehren 
# und Lernen, Technische Universität Dresden
#
# This program is free software: you can redistribute it and/or modify it under
# the terms of the GNU General Public License as published by the Free Software
# Foundation, either version 3 of the License, or (at your option) any later
# version.
#
# This program is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
# FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
# details.
#
# You should have received a copy of the GNU General Public License along with
# this program. If not, see <http://www.gnu.org/licenses/>.
#

import sys,os,time,copy
import gi.repository.Gtk as gtk

def strap(s):
    x = ""
    for c in s:
        if not c.isspace():
            return x
        x += c
    return x

def fill_up(s,l):
    if len(s) >= l:
        return s
    return s + " "*(l-len(s))

def do_sort(a):
    s = [x for x in a]
    s.sort()
    return s

def short_id(current_short_id={"x":0}):
    current_short_id["x"] += 1
    return "%02d"%current_short_id["x"]

def kill_newline(x):
    if x.endswith("\r\n"):
        return x[:-2]
    if x.endswith("\n") or x.endswith("\r"):
        return x[:-1]
    return x
def kill_end_newlines(x):
    bubble = 1
    while bubble:
        bubble = 0
        if x.endswith("\r\n"):
            x = x[:-2]
            bubble = 1
        if x.endswith("\n") or x.endswith("\r"):
            x = x[:-1]
            bubble = 1
    return x

def delim_spacer(x):
    x = x.replace("\t"," ").replace("\n"," ").replace("\r"," ").strip()
    while 1:
        y = x.replace("  "," ")
        if x == y:
            return x
        x = y

def quotable_tokenizer(x):
    tokens = []
    current = ""
    string_delim = "\""
    is_string = 0
    skip = 0
    for (c,lookahead) in zip(x,x[1:]+"\x00"):
        if skip > 0:
            skip -= 1
            continue
        if is_string:
            if c == string_delim:
                if lookahead == string_delim:
                    current += string_delim
                    skip = 1
                else:
                    tokens.append(current)
                    current = ""
                    is_string = 0
            else:
                current += c
        elif c == string_delim:
            if len(current) > 0:
                tokens.append(current)
                current = ""
                is_string = 1
            else:
                is_string = 1
        elif c.isspace():
            if len(current) > 0:
                tokens.append(current)
                current = ""
        else:
            current += c
    if len(current)>0:
        tokens.append(current)
    return tokens

def is_classname(x):
    if x == "":
        return 0
    if not x[0].isupper():
        return 0
    for c in x:
        if c.islower():
            return 1
    return 0 # All uppercase letters are constants


def strip_C(x):
    if x.endswith(".C") or x.endswith(".c") or x.endswith(".h") or x.endswith(".h"):
        return x[:-2]
    return x

def strip_PY(x):
    if x.upper().endswith(".PY"):
        return x[:-3]
    return x

def new_interface(name):
    i = {}
    i["NAME"] = name
    i["ELEMENTS"] = []
    i["ELEMENTS.X"] = []
    i["ELEMENTS.x"] = []
    i["SIGNALS"] = []
    i["SIGNALS.opts"] = []
    i["SIGNALS.which"] = []
    i["SIGNALS.target"] = []
    i["TOPLEVELS"] = []
    i["ADD"] = []
    i["ADD.to"] = []
    i["ADD.opts"] = []
    i["SHOW"] = []
    i["LISTS"] = []
    i["LISTS.types"] = []
    i["LISTS.items"] = []
    i["TEXTBUFFERS"] = []
    return i

def if_pack(i, name, parent, opts):
    if parent == None:
        if len(i["TOPLEVELS"]):
            parent = i["TOPLEVELS"][-1]
        else:
            return
    if opts == None:
        opts = []

    if "NOPACK" in opts:
        if name in i["ADD"]:
            idx = i["ADD"].index(name)
            i["ADD"].pop(idx)
            i["ADD.to"].pop(idx)
            i["ADD.opts"].pop(idx)
        return

    if name in i["ADD"]:
        print("UPDATE")
        idx = i["ADD"].index(name)
        i["ADD.to"][idx] = parent
        print(opts)
        i["ADD.opts"][idx] = opts
    else:
        i["ADD"].append(name)
        i["ADD.to"].append(parent)
        i["ADD.opts"].append(opts)
    
def fresh_name(i):
    while 1:
        name = "x"+short_id()
        if not name in i["ELEMENTS"]:
            return name

#
# Interface code is parsed line wise, where the first token (case-insensitively)
# determines which routine is used to process it. For instance,
#  WINDOW main A B C
# will result in
#  T_window being called.
#

def T_window(i, x, X):
    if i == "DOC":
        return """ identifier

    Creates a new Gtk.Window with the given identifier, you may specify % as
    identifier in order to generate a unique name for this object. Adds the
    identifier to the end of the list of toplevels

    Options:

         EXIT_ON_CLOSE

      Connects "delete_event" to gtk.main_quit.

         NOSHOW

      Do not call show_all() on this element when displaying the interface.

         TITLE "window title"

      Sets the title of the window accordingly.

         SIZE width height

      Sets the requested initial window size accordingly.


"""

    if len(x) < 2 or x[1] == "%":
        print("WARNING: WINDOW without an object name!")
        name = fresh_name(i)
    else:
        name = x[1]

    i["ELEMENTS"].append(name)
    i["ELEMENTS.X"].append(X)
    i["ELEMENTS.x"].append(x)

    if "EXIT_ON_CLOSE" in X:
        i["SIGNALS"].append(name)
        i["SIGNALS.opts"].append([])
        i["SIGNALS.which"].append("delete_event")
        i["SIGNALS.target"].append("gtk.main_quit")
    if not "NOSHOW" in X:
        i["SHOW"].append(name)
    i["TOPLEVELS"].append(name)

# This part generates the code that is used when running PY_build,
# it is supposed to return an array of lines of python code

def PY_build_window(name, x, X, interface):
    code = []
    code.append(name+" = gtk.Window()")
    if "TITLE" in X:
        code.append(name+".set_title("+repr(x[X.index("TITLE")+1])+")")
#TODO: SIZE?
    return code

def T_dialog(i, x, X):
    if i == "DOC":
        return """ identifier

    Creates a new Gtk.Window with the given identifier, you may specify % as
    identifier in order to generate a unique name for this object. Adds the
    identifier to the end of the list of toplevels

    Options:

         CANCEL

      Add a cancel button as dialog choice.

         NOOK

      Suppress the okay button as dialog choice.

         NOSHOW

      Do not call show_all() on this element when displaying the interface.

         TITLE "window title"

      Sets the title of the window accordingly.

         SIZE width height

      Sets the requested initial window size accordingly.


"""

    if len(x) < 2 or x[1] == "%":
        print("WARNING: DIALOG without an object name!")
        name = fresh_name(i)
    else:
        name = x[1]

    i["ELEMENTS"].append(name)
    i["ELEMENTS.X"].append(X)
    i["ELEMENTS.x"].append(x)

    if not "NOSHOW" in X:
        i["SHOW"].append(name)
    i["TOPLEVELS"].append(name+".contents")

def PY_build_dialog(name, x, X, interface):
    code = []
    code.append("btns = []")
    if "CANCEL" in X:
        code.append("btns.append(gtk.STOCK_CANCEL)")
        code.append("btns.append(gtk.ResponseType.CANCEL)")
    if not "NOOK" in X:
        code.append("btns.append(gtk.STOCK_OK)")
        code.append("btns.append(gtk.ResponseType.OK)")
    code.append(name+" = gtk.Dialog(parent=parent,buttons=tuple(btns))")
    if "TITLE" in X:
        code.append(name+".set_title("+repr(x[X.index("TITLE")+1])+")")
    code.append("x["+repr(str(eval(name[2:-1]))+".contents")+"] = "+name+".get_content_area()")

    if "SIZE" in X:
        code.append(name+".set_default_size("+x[X.index("SIZE")+1]+", "+x[X.index("SIZE")+2]+")")

    return code
        
def T_item(i, x, X):
    if i == "DOC":
        return """ identifier column_1 column_2 .. column_n

    Appends a row to the Gtk.ListStore with the given identifier, you may spec-
    ify % as identifier in order to append to the last store in the store list.
    The columns of the row will be read from the parameters column_1 through 
    column_n.

"""

    if len(x) < 2 or x[1] == "%":
        if len(i["LISTS"]) == 0:
            print("Error: ITEM % without previous LIST!")
            return
        name = i["LISTS"][-1]
    else:
        name = x[1]

    idx = i["LISTS"].index(name)
    if idx < 0:
        print("ERROR ITEM",name,": List not found!")
        return
    values = x[2:]
    if len(values) < len(i["LISTS.types"][idx]):
        print("WARNING: Not enough list items (using defaults)!")
        default_values = {"str":"","int":"0","float":"0"}
        for i in range(len(value),len(i["LISTS.types"][idx])):
            value.append(default_values[i["LISTS.types"][idx][i]])
    
    i["LISTS.items"][idx].append(values)

def T_list(i, x, X):
    if i == "DOC":
        return """ identifier column_1 column_2 ... column_n

    Creates a new Gtk.ListStore with the given identifier, you may specify % as
    identifier in order to generate a unique name for this object. Adds the
    identifier to the end of the list of lists. The columns of the list entries
    have the types according to the parameters column_1 through column_n. The
    following types are accepted:
        str    __ string entries
        int    __ integer entries
        float  __ floating point entries

"""

    if len(x) < 2 or x[1] == "%":
        name = fresh_name(i)
    else:
        name = x[1]

    i["ELEMENTS"].append(name)
    i["ELEMENTS.X"].append([X[0],name]+X[1:])
    i["ELEMENTS.x"].append([x[0],name]+x[1:])

    i["LISTS"].append(name)
    i["LISTS.types"].append([t for t in filter(lambda x: x in ["str","int","float"], x[2:])])
    i["LISTS.items"].append([])

def PY_build_list(name, x, X, interface):
    code = []
    idx = interface["LISTS"].index(x[1])
    code.append(name+" = gtk.ListStore("+",".join(interface["LISTS.types"][idx])+")")

    constructor_map = {"str":str,"float":float,"int":int}

    for item in interface["LISTS.items"][idx]:
        line = name+".append(["
        data = [repr(constructor_map[c](v)) for c,v in zip(interface["LISTS.types"][idx],item)]
        line += ",".join(data)
        line += "])"
        code.append(line)
        
    return code

def T_text(i, x, X):
    if i == "DOC":
        return """ identifier

    Creates a new Gtk.TextView with the given identifier, you may specify % as
    identifier in order to generate a unique name for this object. Use inside a
    SCROLLED container to get automatic scroll bars.



    You may specify packing options as well.

"""

    if len(x) < 2 or x[1] == "%":
        name = fresh_name(i)
    else:
        name = x[1]

    i["ELEMENTS"].append(name)
    i["ELEMENTS.X"].append(X)
    i["ELEMENTS.x"].append(x)

    if_pack(i, name, None, X[3:])

def PY_build_text(name, x, X, interface):
    code = []
    code.append(name+" = gtk.TextView()")
    return code


def T_combo(i, x, X):
    if i == "DOC":
        return """ identifier model

    Creates a new Gtk.ComboBox drop down widget with the given identifier, you
    may specify % as identifier in order to generate a unique name or this ob-
    ject. Furthermore, the Gtk.ComboBox is connected to the object identified
    by model, which may be an name of a LIST, or % in order to use the last 
    known list from the list of list stores. The new widget is auto-
    matically packed to the current top-level.

    Options:
      
      COLUMN number

    Use the column given by number (counting starts with 0) instead of the
    default number 0. 

      ACTIVE row

    Set the default active row.


    You may specify packing options as well.

"""

    if len(x) < 2 or x[1] == "%":
        name = fresh_name(i)
    else:
        name = x[1]

    if len(x) < 3 or x[2] == "%":
        if len(i["LISTS"]) == 0:
            print("Error: COMBO without previous LIST!")
            return
        store = i["LISTS"][-1]
    else:
        if not x[2] in i["LISTS"]:
            print("Error: COMBO:",x[2]," -- LIST unknown.")
            return
        store = x[2]

    i["ELEMENTS"].append(name)
    i["ELEMENTS.X"].append([X[0],name,store]+X[3:])
    i["ELEMENTS.x"].append([x[0],name,store]+x[3:])

    if_pack(i, name, None, X[3:])

def PY_build_combo(name, x, X, interface):
    code = []

    code.append(name + " = gtk.ComboBox(model=x["+repr(x[2])+"])")
    col = 0
    if "COLUMN" in X:
        col =x[X.index("COLUMN")+1]

    code.append("renderer = gtk.CellRendererText()")
    code.append(name + ".pack_start(renderer, True)")
    code.append(name + ".add_attribute(renderer, 'text', "+str(col)+")")

    if "ACTIVE" in X:
        code.append(name+".set_active("+x[X.index("ACTIVE")+1]+")")

    return code

def T_entry(i, x, X):
    if i == "DOC":
        return """ identifier text

    Creates a new Gtk.Entry widget with the given identifier, you may specify
    % as identifier in order to generate a unique name or this object. The
    initial contents of the entry are specified by text. The new widget is auto-
    matically packed to the current top-level.

       WIDTH n_chars

    Sets the desired width of the entry widget.

       LENGTH n_chars

    Sets the maximum length of text in the entry widget.

    You may specify packing options as well.

"""

    if len(x) < 2 or x[1] == "%":
        name = fresh_name(i)
    else:
        name = x[1]

    i["ELEMENTS"].append(name)
    i["ELEMENTS.X"].append(X)
    i["ELEMENTS.x"].append(x)

    if_pack(i, name, None, X[3:])

def PY_build_entry(name, x, X, interface):
    code = []

    code.append(name + " = gtk.Entry()")
    if len(x) >= 3:
        code.append(name + ".set_text("+repr(x[2])+")")

    if "WIDTH" in X[3:]:
        code.append(name+".set_width_chars("+x[X.index("WIDTH")+1]+")")
    if "LENGTH" in X[3:]:
        code.append(name+".set_max_length("+x[X.index("WIDTH")+1]+")")

    return code


def T_tree(i, x, X):
    if i == "DOC":
        return """ identifier model

    Creates a new Gtk.TreeView (use inside SCROLLED) with the given iden-
    tifier, you may specify % as identifier in order to generate a unique name
    or this object. Furthermore, the Gtk.TreeView is connected to the object
    identified by model, which may be an name of a LIST, or % in order to use
    the last known list from the list of list stores. The new widget is auto-
    matically packed to the current top-level.

    Options:
      
      SHOWHEADERS

    Show the column headers in the view

    You may specify packing options as well.

"""

    if len(x) < 2 or x[1] == "%":
        name = fresh_name(i)
    else:
        name = x[1]
    if len(x) < 3 or x[2] == "%":
        if len(i["LISTS"]) == 0:
            print("Error: LIST_SELECTION without previous LIST!")
            return
        store = i["LISTS"][-1]
    else:
        if not x[2] in i["LISTS"]:
            print("Error: LIST_SELECTION:",x[2]," -- LIST unknown.")
            return
        store = x[2]

    i["ELEMENTS"].append(name)
    i["ELEMENTS.X"].append([X[0],name,store]+X[3:])
    i["ELEMENTS.x"].append([x[0],name,store]+x[3:])

    if_pack(i, name, None, X[3:])

def PY_build_tree(name, x, X, interface):
    code = []

    code.append(name + " = gtk.TreeView(x["+repr(x[2])+"])")
    if "SHOWHEADERS" in X:
        code.append(name + ".set_property('headers-visible',True)")
    else:
        code.append(name + ".set_property('headers-visible',False)")
    code.append("x["+repr(x[1]+".selection")+"] = "+name+".get_selection()")
    colnr = 0
    for t in interface["LISTS.types"][interface["LISTS"].index(x[2])]:
        # We just default to the Text Cell renderers: TODO change this
        if "TITLES" in X:
            code.append("headername = "+repr(x[X.index("TITLES")+colnr+1]))
        else:
            code.append("headername = None")
        code.append(name+".append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text="+str(colnr)+"))")
        colnr += 1

    return code

        
def T_noconnect(i, x, X):
    if i == "DOC":
        return """ 

    Do not connect signals upon interface build.

"""
    i["NOCONNECT"] = 1

def T_noshow(i, x, X):
    if i == "DOC":
        return """ 

    Do not show widgets upon interface build.

"""
    i["NOCONNECT"] = 1

def T_pack(i, x, X):
    if i == "DOC":
        return """ id-parent id-child [...]

    Packs the object identified by id-child to the toplevel object identified
    by id-parent. The default is to use the add()-method of the parent. You may
    specify % as id-parent in order to use the current top-level widget, and,
    you may also specify % as id-child in order te use the last widget element.

    Options:
    
       START

    Package at the start of the top-level _box_ using pack_start. (May fail
    for some containers.) Use with BOX containers.

       END

    Package at the end of the top-level _box_ using pack_end. (May fail for
    some containers.) Use with BOX containers.

       GRID left top width height

    Package the child using the places (left,top) through including the places
    (left+width-1,top+height-1) in the grid. (May fail for some containers.)
    Use with GRID containers.

       EXPAND

    Use available extra space for the child widget by padding it. Use with BOX
    containers.

       FILL

    Give extra space to the child widget itself. Use with BOX containers.

       HEXPAND
       VEXPAND
       NOHEXPAND
       NOVEXPAND

    Set or unset the expanding properties of a widget. (Works with _all_ wid-
    gets)

       PADDING space

    Use additional spacing around the child according to the value of space.

       SIZEREQUEST width height

    Sets the minimal size of the widget.

       NOPACK

    Do not pack this widget at all. 

"""
    if len(x)<2 or x[1] == "%":
        parent = i["TOPLEVELS"][-1]
    else:
        parent = x[1]
    if len(x)<3 or x[2] == "%":
        name = i["ELEMENTS"][-1]
    else:
        name = x[2]

    if_pack(i,name,parent,X[3:])

def T_connect(i, x, X):
    if i == "DOC":
        return """ identifier signal handler

    Connects the signal of the object identified by identifier with the given 
    handler. You may % as identifier in order to use the last widget element.

    Options:

       REFLECT

    Prepend the parameter list of the handler with the dictionary of the inter-
    face.


"""
    if len(x) < 4:
        print("Warning: parameters missing for "," ".join(x))
    if x[1] == "%":
        name = i["ELEMENTS"][-1]
    else:
        name = x[1]

    i["SIGNALS"].append(name)
    i["SIGNALS.opts"].append(X[4:])
    i["SIGNALS.which"].append(x[2])
    i["SIGNALS.target"].append(x[3])

def T_toplevel(i, x, X):
    if i == "DOC":
        return """ [identifier] [identifier] [...]

    First, removes the last top-level identifier in the list, then adds the 
    identifiers given in the list.


"""
    if len(i["TOPLEVELS"]) > 0:
        i["TOPLEVELS"].pop()
    else:
        if len(x) < 2:
            print("Warning: spare TOPLEVEL command encountered.")

    i["TOPLEVELS"].extend(x[1:])



def T_label(i, x, X):
    if i == "DOC":
        return """ identifier [label]

    Creates a new Gtk.Label with the given identifier, you may specify % as
    identifier in order to generate a unique name for this object. You may
    specify the text of the label optionally. It is automatically packed to
    the current top-level.

    Options:
      
       LINEWRAP

    Sets the line wrap property of the widget.

       WIDTH n_chars

    Sets the desired width of the label widget.

       MAXWIDTH n_chars

    Sets the desired maximum width of the label widget.


    You may specify packing options as well.
"""

    if len(x) < 2 or x[1] == "%":
        name = fresh_name(i)
    else:
        name = x[1]

    i["ELEMENTS"].append(name)
    i["ELEMENTS.X"].append(X)
    i["ELEMENTS.x"].append(x)

    if_pack(i, name, None, X[3:])

def T_button(i, x, X):
    if i == "DOC":
        return """ identifier [label]

    Creates a new Gtk.Button with the given identifier, you may specify % as
    identifier in order to generate a unique name for this object. It is auto-
    matically packed to the current top-level.

    Options:

      LABEL text

    Sets the button's label accordingly.

      IMAGE filename

    Sets the button's image to the contents of the file given by filename.


    You may specify packing options as well.
"""

    if len(x) < 2 or x[1] == "%":
        name = fresh_name(i)
    else:
        name = x[1]

    i["ELEMENTS"].append(name)
    i["ELEMENTS.X"].append(X)
    i["ELEMENTS.x"].append(x)

    if_pack(i, name, None, X[3:])

def T_image(i, x, X):
    if i == "DOC":
        return """ identifier

    Creates a new Gtk.Image with the given identifier, you may specify % as
    identifier in order to generate a unique name for this object. You may
    specify the text of the label optionally. It is automatically packed to
    the current top-level.

    Options:

      FILE filename

    Load the image contents from the file given under filename.

    You may specify packing options as well.
"""

    if len(x) < 2 or x[1] == "%":
        name = fresh_name(i)
    else:
        name = x[1]

    i["ELEMENTS"].append(name)
    i["ELEMENTS.X"].append(X)
    i["ELEMENTS.x"].append(x)

    if_pack(i, name, None, X[3:])

def PY_build_label(name, x, X, interface):
    code = []
    code.append(name+" = gtk.Label()")
    if len(x) >= 3:
        code.append(name+".set_text("+repr(x[2])+")")
    if "LINEWRAP" in X[3:]:
        code.append(name+".set_line_wrap(True)")
    if "WIDTH" in X[3:]:
        code.append(name+".set_width_chars("+x[X.index("WIDTH")+1]+")")
    if "MAXWIDTH" in X[3:]:
        code.append(name+".set_max_width_chars("+x[X.index("MAXWIDTH")+1]+")")

    return code

def PY_build_button(name, x, X, interface):
    code = []
    code.append(name+" = gtk.Button()")
    if "LABEL" in X:
        code.append(name+".set_label("+repr(x[X.index("LABEL")+1])+")")

    if "IMAGE" in X:
        code.append("img = gtk.Image()")
        code.append("img.set_from_file("+repr(x[X.index("IMAGE")+1])+")")
        code.append(name+".set_image(img)")

    return code
def T_check(i, x, X):
    if i == "DOC":
        return """ [identifier]

    Creates a new Gtk.CheckButton with the given identifier, you may specify
    % as identifier in order to generate a unique name for this object. It is 
    automatically packed to the current top-level.

    Options:

      LABEL text

    Sets the text of the label that is associated with the check button to text.

      IMAGE filename

    Sets the button's image to the contents of the file given by filename.

      CHECKED

    Make the check button ticked by default.

    You may specify packing options as well.
"""

    if len(x) < 2 or x[1] == "%":
        name = fresh_name(i)
    else:
        name = x[1]

    i["ELEMENTS"].append(name)
    i["ELEMENTS.X"].append(X)
    i["ELEMENTS.x"].append(x)

    if_pack(i, name, None, X[2:])


def PY_build_check(name, x, X, interface):
    code = []
    code.append(name+" = gtk.CheckButton()")
    if "LABEL" in X:
        code.append(name+".set_label("+repr(x[X.index("LABEL")+1])+")")

    if "IMAGE" in X:
        code.append("img = gtk.Image()")
        code.append("img.set_from_file("+repr(x[X.index("IMAGE")+1])+")")
        code.append(name+".set_image(img)")
    if "CHECKED" in X:
        code.append(name+".set_active(True)")

    return code

def PY_build_image(name, x, X, interface):
    code = []
    code.append(name+" = gtk.Image()")

    if "FILE" in X:
        filename = x[X.index("FILE")+1]
        code.append(name+".set_from_file("+repr(filename)+")")

    return code

def T_grid(i, x, X):
    if i == "DOC":
        return """ [identifier]

    Creates a new Gtk.Grid with the given identifier, you may specify % as
    identifier in order to generate a unique name for this object. It is 
    automatically packed to the current top-level.

    Options:

       COLSPACING n
       ROWSPACING n

    Sets the row or column spacing to n pixels, respectively.

       COLHOMOGENEOUS
       ROWHOMOGENEOUS

    Requests homogeneous size for rows or columns, respectively.

    You may specify packing options as well.
"""

    if len(x) < 2 or x[1] == "%":
        name = fresh_name(i)
    else:
        name = x[1]

    i["ELEMENTS"].append(name)
    i["ELEMENTS.X"].append(X)
    i["ELEMENTS.x"].append(x)

    if_pack(i, name, None, X[3:])
    i["TOPLEVELS"].append(name)

def PY_build_grid(name, x, X, interface):
    code = []
    code.append(name+" = gtk.Grid()")
    if "COLSPACING" in X:
        code.append(name+".set_column_spacing("+x[X.index("COLSPACING")+1]+")")
    if "ROWSPACING" in X:
        code.append(name+".set_row_spacing("+x[X.index("ROWSPACING")+1]+")")
    code.append(name+".set_column_homogeneous("+("True" if "COLHOMOGENEOUS" in X else "False")+")")
    code.append(name+".set_row_homogeneous("+("True" if "ROWHOMOGENEOUS" in X else "False")+")")

    return code

box_common_options_info = """

      SPACING pix

    Sets the spacing between adjacent child widgets to pix.
"""

def box_common_options(name, x, X, code):
    if "SPACING" in X:
        code.append(name+".set_spacing("+x[X.index("SPACING")+1]+")")
    if "HOMOGENOUS" in X:
        code.append(name+".set_homogeneous(True)")
    else:
        code.append(name+".set_homogeneous(False)")

def T_vbox(i, x, X):
    if i == "DOC":
        return """ [identifier]

    Creates a new Gtk.VBox with the given identifier, you may specify % as
    identifier in order to generate a unique name for this object. It is 
    automatically packed to the current top-level.

    Options:""" + box_common_options_info + """ 

    You may specify packing options as well.
"""

    if len(x) < 2 or x[1] == "%":
        name = fresh_name(i)
    else:
        name = x[1]

    i["ELEMENTS"].append(name)
    i["ELEMENTS.X"].append(X)
    i["ELEMENTS.x"].append(x)

    if_pack(i, name, None, X[3:])
    i["TOPLEVELS"].append(name)

def PY_build_vbox(name, x, X, interface):
    code = []
    code.append(name+" = gtk.VBox()")
    box_common_options(name,x,X,code)

    return code

def T_scrolled(i, x, X):
    if i == "DOC":
        return """ [identifier]

    Creates a new Gtk.ScrolledWindow with the given identifier, you may specify
    % as identifier in order to generate a unique name for this object. It is 
    automatically packed to the current top-level.

    You may specify packing options as well.
"""

    if len(x) < 2 or x[1] == "%":
        name = fresh_name(i)
    else:
        name = x[1]

    i["ELEMENTS"].append(name)
    i["ELEMENTS.X"].append(X)
    i["ELEMENTS.x"].append(x)

    if_pack(i, name, None, X[2:])
    i["TOPLEVELS"].append(name)

def PY_build_scrolled(name, x, X, interface):
    code = []
    code.append(name+" = gtk.ScrolledWindow()")

    return code

def T_frame(i, x, X):
    if i == "DOC":
        return """ [identifier]

    Creates a new Gtk.Frame with the given identifier, you may specify % as
    identifier in order to generate a unique name for this object. It is 
    automatically packed to the current top-level.

    Options:

        LABEL [text]

    Sets the label of the frame to the given text.

        SHADOWIN
        SHADOWOUT
        ETCHEDIN
        ETCHEDOUT

    Sets the frame of the label widget.

    You may specify packing options as well.
"""

    if len(x) < 2 or x[1] == "%":
        name = fresh_name(i)
    else:
        name = x[1]

    i["ELEMENTS"].append(name)
    i["ELEMENTS.X"].append(X)
    i["ELEMENTS.x"].append(x)

    if_pack(i, name, None, X[2:])
    i["TOPLEVELS"].append(name)

def PY_build_frame(name, x, X, interface):
    code = []
    code.append(name+" = gtk.Frame()")
    if "LABEL" in X:
        code.append(name+".set_label("+repr(x[X.index("LABEL")+1])+")")

    if "SHADOWIN" in X:
        code.append(name+".set_shadow_type(gtk.ShadowType.IN)")
    if "SHADOWOUT" in X:
        code.append(name+".set_shadow_type(gtk.ShadowType.OUT)")
    if "ETCHEDIN" in X:
        code.append(name+".set_shadow_type(gtk.ShadowType.ETCHED_IN)")
    if "ETCHEDOUT" in X:
        code.append(name+".set_shadow_type(gtk.ShadowType.ETCHED_OUT)")

    return code

def T_hbox(i, x, X):
    if i == "DOC":
        return """ [identifier]

    Creates a new Gtk.HBox with the given identifier, you may specify % as
    identifier in order to generate a unique name for this object. It is 
    automatically packed to the current top-level.

    Options:""" + box_common_options_info + """ 

    You may specify packing options as well.
"""

    if len(x) < 2 or x[1] == "%":
        name = fresh_name(i)
    else:
        name = x[1]

    i["ELEMENTS"].append(name)
    i["ELEMENTS.X"].append(X)
    i["ELEMENTS.x"].append(x)

    if_pack(i, name, None, X[3:])
    i["TOPLEVELS"].append(name)

def PY_build_hbox(name, x, X, interface):
    code = []
    code.append(name+" = gtk.HBox()")
    box_common_options(name,x,X,code)
    return code

def PY_header(contents):
    if not len(contents) or not contents[0].startswith("#!"):
        contents.insert(0,"#!/usr/bin/python3 -i ")
    if len(contents)<2 or contents[1].find("utf-8") < 0:
        contents.insert(1,"# coding: utf-8")
        contents.insert(2,"")

def PY_mark(contents):
    line = 0
    for x in contents:
        y = x.strip().upper()
        if -1 < y.find("#") < y.find("GENERATED") < y.find("CODE") < y.find("ABOVE"):
            return line
        line += 1
    line = len(contents)
    contents.append("")
    contents.append("# All generated code will be placed above this mark.")
    contents.append("")

    return line


def PY_def(contents, name, params, head, tail):
    starts = -1
    line = 0
    for x in contents:
        if delim_spacer(x).startswith("def "+name):
            starts = line
            break
        line += 1
    if starts == -1:
        contents.append("")
        starts = len(contents)
        contents.append("def "+name+"():")
        contents.append("")

    if ("".join(head)+"".join(tail)).strip() == "":
        head.append("pass")
    
    delims = strap(contents[line+1])
    if delims == "": #we also fix wrong python code :)
        delims = "    "
        ends = starts+1
    else:
        for i in range(starts+1,len(contents)):
            if contents[i].startswith(delims):
                ends = i+1
            elif contents[i].strip() != "":
                break

    current_blocks = contents[starts:ends]
    is_manual = 0
    mid_part = []
    for y in current_blocks:
        x = y.upper()
        if -1 < x.find("#") < x.find("END") < x.find("MANUAL") < x.find("EDIT"):
            is_manual = 0
        if is_manual:
            mid_part.append(y)
        if -1 < x.find("#") < x.find("BEGIN") < x.find("MANUAL") < x.find("EDIT"):
            is_manual = 1

    if len(mid_part) == 0:
        mid_part.append(delims)

    generated = []
    generated.append("def "+name+"("+params+"): # do not edit this line.");
    generated.append(delims + "# Automatically generated code. Do not edit manually.");
    generated.append(delims + "# Generated "+time.strftime("%c"))
    generated.extend([delims + x for x in head])
    generated.append(delims + "# Begin of manually editable code section. It is safe to edit below.");
    generated.extend(mid_part)
    generated.append(delims + "# End of manually editable code section. It is NOT safe to edit below.");
    generated.extend([delims + x for x in tail])
    generated.append(delims + "# End of automatically generated code section. Do not edit manually.");

    for i in range(starts,ends):
        contents.pop(starts)

    for i in range(len(generated)):
        contents.insert(starts+i,generated[i])
    if len(contents) > starts+len(generated):
        if contents[starts+len(generated)] != "":
            contents.insert(starts+len(generated),"")

def PY_import(contents, module):
    for x in contents:
        if delim_spacer(x).startswith("import "+module):
            return
    contents.insert(3,"import "+module)

def PY_connect(contents, interface):
    name = interface["NAME"]+"_connect"
    params = "x"
    head = []

    for e,opts,sig,target in zip(interface["SIGNALS"],
                            interface["SIGNALS.opts"],
                            interface["SIGNALS.which"],
                            interface["SIGNALS.target"]):
        if "REFLECT" in opts:
           head.append("x["+repr(e)+"].connect("+repr(sig)+",lambda *t,x=x:"+target+"(x,*t))")
        else:
           head.append("x["+repr(e)+"].connect("+repr(sig)+","+target+")")

    tail = []
    PY_def(contents,name,params,head,tail)

def PY_build(contents, interface):
    identifiers = [x for x in globals() if x.startswith("PY_build_")]
    propagation = {}
    for x in identifiers:
        propagation[x[len("PY_build_"):].upper()] = globals()[x]


    name = interface["NAME"]+"_build"
    params = "x=None,parent=None"
    head = []
    head.append("if x == None:")
    head.append("    x = {}")
    head.append("")

    # convenience members


    # build elements

    for e,x,X in zip(interface["ELEMENTS"],interface["ELEMENTS.x"],interface["ELEMENTS.X"]):
        if X[0] in propagation:
            head.extend(propagation[X[0]]("x["+repr(e)+"]", x, X, interface))
        else:
            print("Warning: PY_build_"+x[0]+" undefined!")

    head.append("")

    # pack elements

    for c,p,opts in zip(interface["ADD"], interface["ADD.to"], interface["ADD.opts"]):
        if "SIZEREQUEST" in opts:
            width = opts[opts.index("SIZEREQUEST")+1]
            height = opts[opts.index("SIZEREQUEST")+2]
            head.append("x["+repr(c)+"].set_size_request("+str(width)+", "+str(height)+")")
        if "HEXPAND" in opts:
            head.append("x["+repr(c)+"].set_hexpand(True)")
        if "VEXPAND" in opts:
            head.append("x["+repr(c)+"].set_vexpand(True)")
        if "NOHEXPAND" in opts:
            head.append("x["+repr(c)+"].set_hexpand(False)")
        if "NOVEXPAND" in opts:
            head.append("x["+repr(c)+"].set_vexpand(False)")
        if "GRID" in opts:
            ps = opts[opts.index("GRID")+1:]
            options = ""
            if "FILL" in opts:
                options = ", gtk.AttachOptions.FILL"
            if "SHRINK" in opts:
                if options == "":
                    options += ", "
                else:
                    options += " | "
                options += "gtk.AttachOptions.SHRINK"
            #options is gtk2, unfortunately
            head.append("x["+repr(p)+"].attach(x["+repr(c)+"],"+",".join(ps[:4])+")")
        elif "START" in opts or "END" in opts or "EXPAND" in opts or "FILL" in opts or "PADDING" in opts:
            cmd = "x["+repr(p)+"].pack_"
            if "END" in opts:
                cmd += "end("
            else:
                cmd += "start("
            cmd += "x["+repr(c)+"]"
            if "EXPAND" in opts or "FILL" in opts:
                cmd += ", True"
            else:
                cmd += ", False"
            if "FILL" in opts:
                cmd += ", True"
            else:
                cmd += ", False"
            if "PADDING" in opts:
                cmd += ", "+opts[opts.index("PADDING")+1]
            else:
                cmd += ", 0";
            cmd += ")"
            head.append(cmd)
        else:
            head.append("x["+repr(p)+"].add(x["+repr(c)+"])")

    head.append("")

    # connect signals
    if not "NOCONNECT" in interface:
        head.append(interface["NAME"]+"_connect(x)")

    if not "NOSHOW" in interface:
        head.append(interface["NAME"]+"_show(x)")


    tail = ["return x"]
    PY_def(contents,name,params,head,tail)
 
def PY_show(contents, interface):
    name = interface["NAME"] + "_show"
    params = "x"
    head = []
    for x in interface["SHOW"]:
        head.append("x["+repr(x)+"].show_all()")
    tail = []
    PY_def(contents,name,params,head,tail)

def PY_hide(contents, interface):
    name = interface["NAME"] + "_hide"
    params = "x"
    head = []
    for x in interface["SHOW"]:
        head.append("x["+repr(x)+"].hide()")
    tail = []
    PY_def(contents,name,params,head,tail)

def update_PY(filename, interface):

    print("Updating "+str(interface["NAME"])+" in "+str(filename)+"...",end="")
    #print(interface);

    contents_all = []
    if os.path.exists(filename+".py"):
        f = open(filename+".py", encoding="utf-8")
        contents_all = [kill_newline(x) for x in f.readlines()]
        f.close()

    original_file_contents = copy.deepcopy(contents_all)
    #print(original_file_contents)


    mark = PY_mark(contents_all)
    contents = contents_all[:mark]
    bottom = contents_all[mark:]


    # the header is merely for convenience
    PY_header(contents)

    #print(contents);

    # import Gtk via gobject-introspection
    PY_import(contents, "gi.repository.Gtk as gtk")

    # create interface functions

    PY_build(contents, interface)
    PY_show(contents, interface)
    PY_connect(contents, interface)


    # add the bottom of the script

    contents.extend(bottom)
    #print(contents);

    no_substantial_changes = len(original_file_contents) + 4 >= len(contents)
    for a,b in zip(original_file_contents,contents):
        #print("<<",a);
        #print(">>",b);
        if a!=b:
            if a.strip().startswith("#") and b.strip().startswith("#"):
                continue
            no_substantial_changes = False
            break

    if no_substantial_changes:
        print(" deferred: no changes to be made.")
        return

    f = open(filename+".py","w",encoding="utf-8")
    for l in contents:
        f.write(l+"\n")
    f.close()
    print(" done.")

def do_updates(files, interface):
    # C output is NOT SUPPORTED
    #c_list = files.get("C",{})
    #for x in c_list:
    #    update_C(x, interface)
    py_list = files.get("PY",{})
    for x in py_list: 
        update_PY(x, interface)

def update(filename):
    """updates the requested source files according to the file given by filename"""
    f = open(filename, encoding="utf-8")
    desc = [quotable_tokenizer(x) for x in f.readlines()]
    desc0 = [x for x in desc if len(x)>0 and not x[0].startswith("#")] # kill comments.
    desc = []
    for line in desc0:
        if line[0].startswith("+") or line[0].startswith("&"):
            if len(line[0]) > 1:
                line[0] = line[0][1:]
            else:
                line.pop(0)
            if len(desc):
                desc[-1].extend(line)
            else:
                desc.append(line)
        else:
            desc.append(line)
            
    DESC = [[q.upper() for q in x] for x in desc]
    dzip = [x for x in zip(desc,DESC)]
    f.close()

    identifiers = [x for x in globals() if x.startswith("T_")]
    propagation = {}
    for x in identifiers:
        propagation[x[2:].upper()] = globals()[x]

    output_files = {}
    for x,X in dzip:
        if X[0] =="C":
            c_list = output_files.get("C",{})
            c_name = strip_C(x[1])
            c_list[c_name] = c_name
            output_files["C"] = c_list
            print("Warning: UNSUPPORTED Output C: ",c_name,".c/h requested!")
        elif X[0] == "PYTHON":
            py_list = output_files.get("PY",{})
            py_name = strip_PY(x[1])
            py_list[py_name] = py_name
            output_files["PY"] = py_list
            print("Output Python: ",py_name,".py")

    interface = new_interface(os.path.basename(filename))

    for x,X in dzip:
        if X[0] == "INTERFACE":
            interface = new_interface(x[1])
        elif X[0] == "END":
            if interface["NAME"] != x[1]:
                print("Error: END ",x[1]," given, but END ",interface["NAME"]," expected!")
            else:
                print("Updating ",x[1])
                do_updates(output_files,interface)
        elif X[0] == "PYTHON" or X[0] == "C":
            pass
        else:
            if X[0] in propagation:
                propagation[X[0]](interface,x,X)
            else:
                print("Error: identifier ",X[0]," unknown.")
            
# fix the docs

info = sys.argv[0] + """ creates Python code for GTK interfaces.

 Input file format
 =================

 The input files are plain text files, that contain line by line instructions.
 If a single command spans more than one line, all but the first lines must be
 prefixed by + or &.

 Example:
   >    WINDOW win
   >       + TITLE "Window Title"

 Comments
 --------

 If the first non-space character on a line is #, then this line is regarded
 to be a comment.

   >    #  This line is a comment



 Generating Output
 -----------------

 The following commands generate output

    PY FILENAME
    PY FILENAME.py

 These two commands both generate the same output to the file FILENAME.py.
 The file will be updated if it already exists, and previously generated code
 parts will be replaced if the files already contain generated code.

 Please note that it is possible to define any number of output files.


 Describing GTK Interfaces
 --------------------------

 Each data structure is describe in a block that starts with
    
     INTERFACE if_name

 and that ends with

     END if_name


 The following interface specification commands are currently supported:

"""

for fn in dir():
    if fn.startswith("T_"):
        info += fn[2:].upper() + kill_end_newlines(globals()[fn]("DOC",None,None))+"\n\n"


 
# main script

if len(sys.argv) < 2:
    print("""Usage: %s file1 [file2 ...]
    Generates/updates the appropriate code files according to the data
    structure descriptions found in the input files.

%s --help
    print help on input data format    
    """%(sys.argv[0],sys.argv[0]))
elif sys.argv[1] == "--help" and len(sys.argv)==2:
    print(info)
else:
    for p in sys.argv[1:]:
        update(p)
