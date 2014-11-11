#!/usr/bin/python3
# coding: utf-8
#
# complexlearningtasks.py, (c) 2014, Immanuel Albrecht; Professur für Lehren
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

import sys,os,time
import uuid
import copy,re
import gi.repository.Gtk as gtk
import gi.repository.Gdk as gdk
from functools import cmp_to_key


def edit_sentence_templates_build(x=None,parent=None): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    if x == None:
        x = {}
    
    btns = []
    btns.append(gtk.STOCK_CANCEL)
    btns.append(gtk.ResponseType.CANCEL)
    btns.append(gtk.STOCK_OK)
    btns.append(gtk.ResponseType.OK)
    x['dlg'] = gtk.Dialog(parent=parent,buttons=tuple(btns))
    x['dlg'].set_title('Edit Sentence Template...')
    x['dlg.contents'] = x['dlg'].get_content_area()
    x['dlg'].set_default_size(800, 500)
    x['g0'] = gtk.Grid()
    x['g0'].set_column_spacing(5)
    x['g0'].set_row_spacing(5)
    x['g0'].set_column_homogeneous(False)
    x['g0'].set_row_homogeneous(False)
    x['x01'] = gtk.Frame()
    x['x01'].set_label('Building Blocks')
    x['x01'].set_shadow_type(gtk.ShadowType.ETCHED_IN)
    x['g1'] = gtk.Grid()
    x['g1'].set_column_spacing(2)
    x['g1'].set_row_spacing(2)
    x['g1'].set_column_homogeneous(False)
    x['g1'].set_row_homogeneous(False)
    x['l0'] = gtk.Label()
    x['l0'].set_text('Please enter example sentences of the template below, use a new line for each instance and divide the sentence into parts using slashes (/):')
    x['l0'].set_line_wrap(True)
    x['l0'].set_max_width_chars(65)
    x['p1'] = gtk.Button()
    x['p1'].set_label('Preview...')
    x['s0'] = gtk.ScrolledWindow()
    x['exs'] = gtk.TextView()
    x['x02'] = gtk.Frame()
    x['x02'].set_label('Collocation Constraints')
    x['x02'].set_shadow_type(gtk.ShadowType.ETCHED_IN)
    x['g2'] = gtk.Grid()
    x['g2'].set_column_homogeneous(False)
    x['g2'].set_row_homogeneous(False)
    x['l1'] = gtk.Label()
    x['l1'].set_text('Please enter the constraints for the individual block choices below, use a new line for each constraint, divide parts using slashes (/), and use a single star (*) as a single part wildcard, and a double star (**) as a multiple parts wildcard:')
    x['l1'].set_line_wrap(True)
    x['l1'].set_max_width_chars(65)
    x['p2'] = gtk.Button()
    x['p2'].set_label('Preview...')
    x['s1'] = gtk.ScrolledWindow()
    x['cons'] = gtk.TextView()
    x['x03'] = gtk.Frame()
    x['x03'].set_label('Non-interactive Blocks')
    x['x03'].set_shadow_type(gtk.ShadowType.ETCHED_IN)
    x['g3'] = gtk.Grid()
    x['g3'].set_column_homogeneous(False)
    x['g3'].set_row_homogeneous(False)
    x['x04'] = gtk.Label()
    x['x04'].set_text('The following list contains the indexes of blocks which are not to be selected by the student, counted from the left starting at 1 and separated by commas (,).')
    x['x04'].set_line_wrap(True)
    x['p3'] = gtk.Button()
    x['p3'].set_label('Preview...')
    x['noninter'] = gtk.Entry()
    x['noninter'].set_text('')
    x['nouser'] = gtk.CheckButton()
    x['nouser'].set_label('Exclude this template from user input.')
    
    x['dlg.contents'].add(x['g0'])
    x['g0'].attach(x['x01'],0,0,1,1)
    x['x01'].add(x['g1'])
    x['g1'].attach(x['l0'],1,1,1,1)
    x['g1'].attach(x['p1'],2,1,1,1)
    x['s0'].set_size_request(550, 110)
    x['s0'].set_hexpand(True)
    x['s0'].set_vexpand(True)
    x['g1'].attach(x['s0'],1,2,2,5)
    x['s0'].add(x['exs'])
    x['g0'].attach(x['x02'],0,1,1,1)
    x['x02'].add(x['g2'])
    x['g2'].attach(x['l1'],1,7,1,1)
    x['p2'].set_vexpand(False)
    x['g2'].attach(x['p2'],2,7,1,1)
    x['s1'].set_size_request(550, 110)
    x['s1'].set_hexpand(True)
    x['s1'].set_vexpand(True)
    x['g2'].attach(x['s1'],1,8,2,5)
    x['s1'].add(x['cons'])
    x['g0'].attach(x['x03'],0,2,1,1)
    x['x03'].add(x['g3'])
    x['g3'].attach(x['x04'],1,1,7,1)
    x['p3'].set_vexpand(False)
    x['g3'].attach(x['p3'],8,1,1,1)
    x['g3'].attach(x['noninter'],1,2,8,1)
    x['g3'].attach(x['nouser'],1,3,8,1)
    
    edit_sentence_templates_connect(x)
    edit_sentence_templates_show(x)
    # Begin of manually editable code section. It is safe to edit below.
    print("BUILD edit_sentence_templates")
    # End of manually editable code section. It is NOT safe to edit below.
    return x
    # End of automatically generated code section. Do not edit manually.


def edit_sentence_templates_show(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    x['dlg'].show_all()
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def edit_sentence_templates_connect(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    x['p1'].connect('clicked',lambda *t,x=x:preview_template1(x,*t))
    x['p2'].connect('clicked',lambda *t,x=x:preview_template2(x,*t))
    x['p3'].connect('clicked',lambda *t,x=x:preview_template3(x,*t))
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def edit_sentence_templates2_build(x=None,parent=None): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Wed 03 Sep 2014 10:30:09 AM CEST
    if x == None:
        x = {}
    
    x['dlg'] = gtk.Window()
    x['dlg'].set_title('Edit Sentence Template...')
    x['uuidB91C14'] = gtk.VBox()
    x['uuidB91C14'].set_spacing(3)
    x['uuidB91C14'].set_homogeneous(False)
    x['uuidA419E8'] = gtk.Label()
    x['uuidA419E8'].set_text('Please enter example sentences of the template below, use a new line for each instance and divide the sentence into parts using slashes (/):')
    x['uuidA419E8'].set_line_wrap(True)
    x['uuid9D8CD8'] = gtk.ScrolledWindow()
    x['exs'] = gtk.TextView()
    x['uuid37A3C7'] = gtk.Label()
    x['uuid37A3C7'].set_text('Please enter examples of dependencies between different template parts, use a star (*) in order to indicate a free template part:')
    x['uuid37A3C7'].set_line_wrap(True)
    x['uuid341282'] = gtk.ScrolledWindow()
    x['deps'] = gtk.TextView()
    x['uuid9220EA'] = gtk.Label()
    x['uuid9220EA'].set_text('')
    x['uuidD5A909'] = gtk.HBox()
    x['uuidD5A909'].set_homogeneous(False)
    x['uuid932616'] = gtk.Label()
    x['uuid932616'].set_text('')
    x['uuid966D92'] = gtk.Button()
    x['uuid966D92'].set_label('Cancel')
    x['uuid0B6994'] = gtk.Button()
    x['uuid0B6994'].set_label('OK')
    x['uuidF7C656'] = gtk.Button()
    x['uuidF7C656'].set_label('Preview...')
    x['uuid5CB8C6'] = gtk.Label()
    x['uuid5CB8C6'].set_text('')
    x['uuidFE5652'] = gtk.Label()
    x['uuidFE5652'].set_text('')
    x['uuid1856C7'] = gtk.Label()
    x['uuid1856C7'].set_text('')
    x['uuid9A72D9'] = gtk.Label()
    x['uuid9A72D9'].set_text('')
    
    x['dlg'].add(x['uuidB91C14'])
    x['uuidB91C14'].pack_start(x['uuidA419E8'], False, False, 0)
    x['uuid9D8CD8'].set_size_request(100, 65)
    x['uuidB91C14'].pack_start(x['uuid9D8CD8'], False, False, 0)
    x['uuid9D8CD8'].add(x['exs'])
    x['uuidB91C14'].pack_start(x['uuid37A3C7'], False, False, 0)
    x['uuid341282'].set_size_request(100, 65)
    x['uuidB91C14'].pack_start(x['uuid341282'], False, False, 0)
    x['uuid341282'].add(x['deps'])
    x['uuid9220EA'].set_vexpand(True)
    x['uuidB91C14'].pack_start(x['uuid9220EA'], True, True, 0)
    x['uuidD5A909'].set_hexpand(True)
    x['uuidD5A909'].set_vexpand(False)
    x['uuidB91C14'].add(x['uuidD5A909'])
    x['uuid932616'].set_hexpand(True)
    x['uuid932616'].set_vexpand(False)
    x['uuidD5A909'].pack_start(x['uuid932616'], False, False, 0)
    x['uuid966D92'].set_vexpand(False)
    x['uuidD5A909'].pack_start(x['uuid966D92'], False, False, 0)
    x['uuid0B6994'].set_vexpand(False)
    x['uuidD5A909'].pack_start(x['uuid0B6994'], False, False, 0)
    x['uuidF7C656'].set_vexpand(False)
    x['uuidD5A909'].pack_start(x['uuidF7C656'], False, False, 0)
    x['uuid5CB8C6'].set_vexpand(True)
    x['uuidB91C14'].pack_start(x['uuid5CB8C6'], True, True, 0)
    x['uuidFE5652'].set_vexpand(True)
    x['uuidB91C14'].pack_start(x['uuidFE5652'], True, True, 0)
    x['uuid1856C7'].set_vexpand(True)
    x['uuidB91C14'].pack_start(x['uuid1856C7'], True, True, 0)
    x['uuid9A72D9'].set_vexpand(True)
    x['uuidB91C14'].pack_start(x['uuid9A72D9'], True, True, 0)
    
    edit_sentence_templates2_connect(x)
    edit_sentence_templates2_show(x)
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    return x
    # End of automatically generated code section. Do not edit manually.


def edit_sentence_templates2_show(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Wed 03 Sep 2014 10:30:09 AM CEST
    x['dlg'].show_all()
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def edit_sentence_templates2_connect(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Wed 03 Sep 2014 10:30:09 AM CEST
    x['dlg'].connect('delete_event',gtk.main_quit)
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def template_preview_dlg_build(x=None,parent=None): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 16 Sep 2014 11:36:12 AM CEST
    if x == None:
        x = {}
    
    btns = []
    btns.append(gtk.STOCK_OK)
    btns.append(gtk.ResponseType.OK)
    x['dlg'] = gtk.Dialog(parent=parent,buttons=tuple(btns))
    x['dlg'].set_title('Preview...')
    x['dlg.contents'] = x['dlg'].get_content_area()
    x['h'] = gtk.HBox()
    x['h'].set_homogeneous(False)
    
    x['dlg.contents'].add(x['h'])
    
    template_preview_dlg_connect(x)
    template_preview_dlg_show(x)
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    return x
    # End of automatically generated code section. Do not edit manually.


def template_preview_dlg_show(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 16 Sep 2014 11:36:12 AM CEST
    x['dlg'].show_all()
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def template_preview_dlg_connect(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 16 Sep 2014 11:36:12 AM CEST
    pass
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def ignored_exs_dlg_build(x=None,parent=None): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    if x == None:
        x = {}
    
    btns = []
    btns.append(gtk.STOCK_OK)
    btns.append(gtk.ResponseType.OK)
    x['dlg'] = gtk.Dialog(parent=parent,buttons=tuple(btns))
    x['dlg'].set_title('Error List -- Preview...')
    x['dlg.contents'] = x['dlg'].get_content_area()
    x['v'] = gtk.VBox()
    x['v'].set_homogeneous(False)
    x['x06'] = gtk.Label()
    x['x06'].set_text('The following sentence template examples have been ignored:')
    x['s0'] = gtk.ScrolledWindow()
    x['l0'] = gtk.ListStore(str,str)
    x['t0'] = gtk.TreeView(x['l0'])
    x['t0'].set_property('headers-visible',True)
    x['t0.selection'] = x['t0'].get_selection()
    headername = 'Example'
    x['t0'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=0))
    headername = 'Ignored because...'
    x['t0'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=1))
    
    x['dlg.contents'].add(x['v'])
    x['x06'].set_vexpand(False)
    x['v'].add(x['x06'])
    x['s0'].set_size_request(850, 400)
    x['s0'].set_hexpand(True)
    x['s0'].set_vexpand(True)
    x['v'].add(x['s0'])
    x['s0'].add(x['t0'])
    
    ignored_exs_dlg_connect(x)
    ignored_exs_dlg_show(x)
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    return x
    # End of automatically generated code section. Do not edit manually.


def ignored_exs_dlg_show(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    x['dlg'].show_all()
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def ignored_exs_dlg_connect(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    pass
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def ignored_exs_dlg2_build(x=None,parent=None): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    if x == None:
        x = {}
    
    btns = []
    btns.append(gtk.STOCK_OK)
    btns.append(gtk.ResponseType.OK)
    x['dlg'] = gtk.Dialog(parent=parent,buttons=tuple(btns))
    x['dlg'].set_title('Error List -- Preview...')
    x['dlg.contents'] = x['dlg'].get_content_area()
    x['v'] = gtk.VBox()
    x['v'].set_homogeneous(False)
    x['x07'] = gtk.Label()
    x['x07'].set_text('The following constraint patterns have been ignored:')
    x['s0'] = gtk.ScrolledWindow()
    x['l0'] = gtk.ListStore(str,str)
    x['t0'] = gtk.TreeView(x['l0'])
    x['t0'].set_property('headers-visible',True)
    x['t0.selection'] = x['t0'].get_selection()
    headername = 'Constraint Pattern'
    x['t0'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=0))
    headername = 'Ignored because...'
    x['t0'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=1))
    
    x['dlg.contents'].add(x['v'])
    x['x07'].set_vexpand(False)
    x['v'].add(x['x07'])
    x['s0'].set_size_request(850, 400)
    x['s0'].set_hexpand(True)
    x['s0'].set_vexpand(True)
    x['v'].add(x['s0'])
    x['s0'].add(x['t0'])
    
    ignored_exs_dlg2_connect(x)
    ignored_exs_dlg2_show(x)
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    return x
    # End of automatically generated code section. Do not edit manually.


def ignored_exs_dlg2_show(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    x['dlg'].show_all()
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def ignored_exs_dlg2_connect(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    pass
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def list_preview_dlg_build(x=None,parent=None): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    if x == None:
        x = {}
    
    btns = []
    btns.append(gtk.STOCK_OK)
    btns.append(gtk.ResponseType.OK)
    x['dlg'] = gtk.Dialog(parent=parent,buttons=tuple(btns))
    x['dlg'].set_title('Preview...')
    x['dlg.contents'] = x['dlg'].get_content_area()
    x['v'] = gtk.VBox()
    x['v'].set_homogeneous(False)
    x['x08'] = gtk.Label()
    x['x08'].set_text('The following sentences are valid picks:')
    x['s0'] = gtk.ScrolledWindow()
    x['l0'] = gtk.ListStore(str)
    x['t0'] = gtk.TreeView(x['l0'])
    x['t0'].set_property('headers-visible',False)
    x['t0.selection'] = x['t0'].get_selection()
    headername = None
    x['t0'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=0))
    
    x['dlg.contents'].add(x['v'])
    x['x08'].set_vexpand(False)
    x['v'].add(x['x08'])
    x['s0'].set_size_request(850, 400)
    x['s0'].set_hexpand(True)
    x['s0'].set_vexpand(True)
    x['v'].add(x['s0'])
    x['s0'].add(x['t0'])
    
    list_preview_dlg_connect(x)
    list_preview_dlg_show(x)
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    return x
    # End of automatically generated code section. Do not edit manually.


def list_preview_dlg_show(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    x['dlg'].show_all()
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def list_preview_dlg_connect(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    pass
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def ui_demo_dlg_build(x=None,parent=None): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    if x == None:
        x = {}
    
    btns = []
    btns.append(gtk.STOCK_OK)
    btns.append(gtk.ResponseType.OK)
    x['dlg'] = gtk.Dialog(parent=parent,buttons=tuple(btns))
    x['dlg'].set_title('User Interaction - Preview...')
    x['dlg.contents'] = x['dlg'].get_content_area()
    x['v'] = gtk.VBox()
    x['v'].set_homogeneous(False)
    x['x09'] = gtk.Label()
    x['x09'].set_text('You specified the following user interface:')
    
    x['dlg.contents'].add(x['v'])
    x['v'].add(x['x09'])
    
    ui_demo_dlg_connect(x)
    ui_demo_dlg_show(x)
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    return x
    # End of automatically generated code section. Do not edit manually.


def ui_demo_dlg_show(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    x['dlg'].show_all()
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def ui_demo_dlg_connect(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    pass
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def clt_dlg_build(x=None,parent=None): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 07 Oct 2014 02:12:37 PM CEST
    if x == None:
        x = {}
    
    btns = []
    btns.append(gtk.STOCK_CANCEL)
    btns.append(gtk.ResponseType.CANCEL)
    btns.append(gtk.STOCK_OK)
    btns.append(gtk.ResponseType.OK)
    x['dlg'] = gtk.Dialog(parent=parent,buttons=tuple(btns))
    x['dlg'].set_title('Complex Learning Task Editor')
    x['dlg.contents'] = x['dlg'].get_content_area()
    x['v'] = gtk.VBox()
    x['v'].set_homogeneous(False)
    x['x65'] = gtk.Frame()
    x['x65'].set_label('Point Templates')
    x['x65'].set_shadow_type(gtk.ShadowType.ETCHED_IN)
    x['x66'] = gtk.Grid()
    x['x66'].set_column_homogeneous(False)
    x['x66'].set_row_homogeneous(False)
    x['sp'] = gtk.ScrolledWindow()
    x['lp'] = gtk.ListStore(int,str,str)
    x['tp'] = gtk.TreeView(x['lp'])
    x['tp'].set_property('headers-visible',True)
    x['tp.selection'] = x['tp'].get_selection()
    headername = '#'
    x['tp'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=0))
    headername = 'User?'
    x['tp'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=1))
    headername = 'Sample'
    x['tp'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=2))
    x['x67'] = gtk.Button()
    x['x67'].set_label('New...')
    x['x68'] = gtk.Button()
    x['x68'].set_label('Edit..')
    x['x69'] = gtk.Button()
    x['x69'].set_label('Copy')
    x['x70'] = gtk.Button()
    x['x70'].set_label('Paste')
    x['x71'] = gtk.Button()
    x['x71'].set_label('Cut')
    x['x72'] = gtk.Button()
    x['x72'].set_label('List all...')
    x['x73'] = gtk.Button()
    x['x73'].set_label('Preview...')
    x['x74'] = gtk.Frame()
    x['x74'].set_label('Inference Step Rules')
    x['x74'].set_shadow_type(gtk.ShadowType.ETCHED_IN)
    x['x75'] = gtk.Grid()
    x['x75'].set_column_homogeneous(False)
    x['x75'].set_row_homogeneous(False)
    x['x76'] = gtk.ScrolledWindow()
    x['lr'] = gtk.ListStore(int,str,str,str,str)
    x['tr'] = gtk.TreeView(x['lr'])
    x['tr'].set_property('headers-visible',True)
    x['tr.selection'] = x['tr'].get_selection()
    headername = '#'
    x['tr'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=0))
    headername = 'Jp?'
    x['tr'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=1))
    headername = 'Tp?'
    x['tr'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=2))
    headername = 'Tr?'
    x['tr'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=3))
    headername = 'Rule Description'
    x['tr'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=4))
    x['x77'] = gtk.Button()
    x['x77'].set_label('New...')
    x['x78'] = gtk.Button()
    x['x78'].set_label('Edit..')
    x['x79'] = gtk.Button()
    x['x79'].set_label('Copy')
    x['x80'] = gtk.Button()
    x['x80'].set_label('Paste')
    x['x81'] = gtk.Button()
    x['x81'].set_label('Cut')
    x['x82'] = gtk.Button()
    x['x82'].set_label('List justified...')
    x['x83'] = gtk.Button()
    x['x83'].set_label('List trivial...')
    x['x84'] = gtk.Button()
    x['x84'].set_label('Examine...')
    x['x85'] = gtk.Button()
    x['x85'].set_label('f(x)...')
    x['x86'] = gtk.Frame()
    x['x86'].set_label('Expert Points')
    x['x86'].set_shadow_type(gtk.ShadowType.ETCHED_IN)
    x['x87'] = gtk.Frame()
    x['x87'].set_label('Feedback')
    x['x87'].set_shadow_type(gtk.ShadowType.ETCHED_IN)
    
    x['dlg.contents'].add(x['v'])
    x['v'].add(x['x65'])
    x['x65'].add(x['x66'])
    x['sp'].set_size_request(500, 100)
    x['sp'].set_hexpand(True)
    x['sp'].set_vexpand(True)
    x['x66'].attach(x['sp'],0,0,6,3)
    x['sp'].add(x['tp'])
    x['x67'].set_hexpand(False)
    x['x67'].set_vexpand(False)
    x['x66'].attach(x['x67'],6,0,1,1)
    x['x68'].set_hexpand(False)
    x['x68'].set_vexpand(False)
    x['x66'].attach(x['x68'],6,2,1,1)
    x['x69'].set_hexpand(False)
    x['x69'].set_vexpand(False)
    x['x66'].attach(x['x69'],7,0,1,1)
    x['x70'].set_hexpand(False)
    x['x70'].set_vexpand(False)
    x['x66'].attach(x['x70'],7,1,1,1)
    x['x71'].set_hexpand(False)
    x['x71'].set_vexpand(False)
    x['x66'].attach(x['x71'],7,2,1,1)
    x['x72'].set_hexpand(False)
    x['x72'].set_vexpand(False)
    x['x66'].attach(x['x72'],1,4,2,1)
    x['x73'].set_hexpand(False)
    x['x73'].set_vexpand(False)
    x['x66'].attach(x['x73'],4,4,2,1)
    x['v'].add(x['x74'])
    x['x74'].add(x['x75'])
    x['x76'].set_size_request(500, 100)
    x['x76'].set_hexpand(True)
    x['x76'].set_vexpand(True)
    x['x75'].attach(x['x76'],0,0,6,3)
    x['x76'].add(x['tr'])
    x['x77'].set_hexpand(False)
    x['x77'].set_vexpand(False)
    x['x75'].attach(x['x77'],6,0,1,1)
    x['x78'].set_hexpand(False)
    x['x78'].set_vexpand(False)
    x['x75'].attach(x['x78'],6,2,1,1)
    x['x79'].set_hexpand(False)
    x['x79'].set_vexpand(False)
    x['x75'].attach(x['x79'],7,0,1,1)
    x['x80'].set_hexpand(False)
    x['x80'].set_vexpand(False)
    x['x75'].attach(x['x80'],7,1,1,1)
    x['x81'].set_hexpand(False)
    x['x81'].set_vexpand(False)
    x['x75'].attach(x['x81'],7,2,1,1)
    x['x82'].set_hexpand(False)
    x['x82'].set_vexpand(False)
    x['x75'].attach(x['x82'],0,4,2,1)
    x['x83'].set_hexpand(False)
    x['x83'].set_vexpand(False)
    x['x75'].attach(x['x83'],2,4,2,1)
    x['x84'].set_hexpand(False)
    x['x84'].set_vexpand(False)
    x['x75'].attach(x['x84'],4,4,2,1)
    x['x85'].set_hexpand(False)
    x['x85'].set_vexpand(False)
    x['x75'].attach(x['x85'],7,4,1,1)
    x['v'].add(x['x86'])
    x['v'].add(x['x87'])
    
    clt_dlg_connect(x)
    clt_dlg_show(x)
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    return x
    # End of automatically generated code section. Do not edit manually.


def clt_dlg_show(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 07 Oct 2014 02:12:37 PM CEST
    x['dlg'].show_all()
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def clt_dlg_connect(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 07 Oct 2014 02:12:37 PM CEST
    x['x67'].connect('clicked',lambda *t,x=x:clt_new_template(x,*t))
    x['x68'].connect('clicked',lambda *t,x=x:clt_edit_template(x,*t))
    x['x69'].connect('clicked',lambda *t,x=x:clt_copy_template(x,*t))
    x['x70'].connect('clicked',lambda *t,x=x:clt_paste_template(x,*t))
    x['x71'].connect('clicked',lambda *t,x=x:clt_cut_template(x,*t))
    x['x72'].connect('clicked',lambda *t,x=x:clt_list_all_templates(x,*t))
    x['x73'].connect('clicked',lambda *t,x=x:clt_preview_all_templates(x,*t))
    x['x77'].connect('clicked',lambda *t,x=x:clt_ir_new(x,*t))
    x['x78'].connect('clicked',lambda *t,x=x:clt_ir_edit(x,*t))
    x['x79'].connect('clicked',lambda *t,x=x:clt_ir_copy(x,*t))
    x['x80'].connect('clicked',lambda *t,x=x:clt_ir_paste(x,*t))
    x['x81'].connect('clicked',lambda *t,x=x:clt_ir_cut(x,*t))
    x['x82'].connect('clicked',lambda *t,x=x:clt_ir_list_justified(x,*t))
    x['x83'].connect('clicked',lambda *t,x=x:clt_ir_list_trivial(x,*t))
    x['x84'].connect('clicked',lambda *t,x=x:clt_ir_examine(x,*t))
    x['x85'].connect('clicked',lambda *t,x=x:clt_ir_fns(x,*t))
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def templates_preview_dlg_build(x=None,parent=None): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    if x == None:
        x = {}
    
    btns = []
    btns.append(gtk.STOCK_OK)
    btns.append(gtk.ResponseType.OK)
    x['dlg'] = gtk.Dialog(parent=parent,buttons=tuple(btns))
    x['dlg'].set_title('Preview...')
    x['dlg.contents'] = x['dlg'].get_content_area()
    x['x05'] = gtk.Frame()
    x['x05'].set_label('Sentence Template User Interface')
    x['x05'].set_shadow_type(gtk.ShadowType.ETCHED_IN)
    x['h'] = gtk.VBox()
    x['h'].set_homogeneous(False)
    
    x['dlg.contents'].add(x['x05'])
    x['x05'].add(x['h'])
    
    templates_preview_dlg_connect(x)
    templates_preview_dlg_show(x)
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    return x
    # End of automatically generated code section. Do not edit manually.


def templates_preview_dlg_show(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    x['dlg'].show_all()
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def templates_preview_dlg_connect(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    pass
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def pattern_dlg_build(x=None,parent=None): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    if x == None:
        x = {}
    
    btns = []
    btns.append(gtk.STOCK_CANCEL)
    btns.append(gtk.ResponseType.CANCEL)
    btns.append(gtk.STOCK_OK)
    btns.append(gtk.ResponseType.OK)
    x['dlg'] = gtk.Dialog(parent=parent,buttons=tuple(btns))
    x['dlg'].set_title('Sentence Pattern Editor')
    x['dlg.contents'] = x['dlg'].get_content_area()
    x['g0'] = gtk.Grid()
    x['g0'].set_column_homogeneous(False)
    x['g0'].set_row_homogeneous(False)
    x['x10'] = gtk.Label()
    x['x10'].set_text('Description:')
    x['desc'] = gtk.Entry()
    x['desc'].set_text('')
    x['slash'] = gtk.CheckButton()
    x['slash'].set_label('slash-mode')
    x['case'] = gtk.CheckButton()
    x['case'].set_label('case-sensitive mode')
    x['x11'] = gtk.Label()
    x['x11'].set_text('Regular Expression:')
    x['regexp'] = gtk.Entry()
    x['regexp'].set_text('')
    x['x12'] = gtk.Button()
    x['x12'].set_label('Match')
    x['x13'] = gtk.Frame()
    x['x13'].set_label('Matches:')
    x['x14'] = gtk.ScrolledWindow()
    x['lp'] = gtk.ListStore(str)
    x['tp'] = gtk.TreeView(x['lp'])
    x['tp'].set_property('headers-visible',False)
    x['tp.selection'] = x['tp'].get_selection()
    headername = None
    x['tp'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=0))
    
    x['g0'].set_hexpand(True)
    x['dlg.contents'].add(x['g0'])
    x['x10'].set_hexpand(False)
    x['x10'].set_vexpand(False)
    x['g0'].attach(x['x10'],0,0,2,1)
    x['desc'].set_hexpand(True)
    x['desc'].set_vexpand(False)
    x['g0'].attach(x['desc'],2,0,9,1)
    x['slash'].set_hexpand(False)
    x['slash'].set_vexpand(False)
    x['g0'].attach(x['slash'],2,1,2,1)
    x['case'].set_hexpand(False)
    x['case'].set_vexpand(False)
    x['g0'].attach(x['case'],5,1,3,1)
    x['x11'].set_hexpand(False)
    x['x11'].set_vexpand(False)
    x['g0'].attach(x['x11'],0,2,3,1)
    x['regexp'].set_hexpand(True)
    x['regexp'].set_vexpand(False)
    x['g0'].attach(x['regexp'],3,2,7,1)
    x['x12'].set_hexpand(False)
    x['x12'].set_vexpand(False)
    x['g0'].attach(x['x12'],10,2,1,1)
    x['x13'].set_hexpand(True)
    x['x13'].set_vexpand(True)
    x['g0'].attach(x['x13'],0,3,11,8)
    x['x14'].set_size_request(600, 250)
    x['x14'].set_hexpand(True)
    x['x14'].set_vexpand(True)
    x['x13'].add(x['x14'])
    x['x14'].add(x['tp'])
    
    pattern_dlg_connect(x)
    pattern_dlg_show(x)
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    return x
    # End of automatically generated code section. Do not edit manually.


def pattern_dlg_show(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    x['dlg'].show_all()
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def pattern_dlg_connect(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 30 Sep 2014 10:06:13 AM CEST
    x['x12'].connect('clicked',lambda *t,x=x:match_pattern_preview(x,*t))
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def rule_dlg_build(x=None,parent=None): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Mon 10 Nov 2014 02:39:06 PM CET
    if x == None:
        x = {}
    
    btns = []
    btns.append(gtk.STOCK_CANCEL)
    btns.append(gtk.ResponseType.CANCEL)
    btns.append(gtk.STOCK_OK)
    btns.append(gtk.ResponseType.OK)
    x['dlg'] = gtk.Dialog(parent=parent,buttons=tuple(btns))
    x['dlg'].set_title('Inference Step Rule Editor')
    x['dlg.contents'] = x['dlg'].get_content_area()
    x['g0'] = gtk.Grid()
    x['g0'].set_column_homogeneous(False)
    x['g0'].set_row_homogeneous(False)
    x['x15'] = gtk.Label()
    x['x15'].set_text('Description:')
    x['desc'] = gtk.Entry()
    x['desc'].set_text('')
    x['justified'] = gtk.CheckButton()
    x['justified'].set_label('All premises of this rule that meet the conditions shall be considered justified.')
    x['trivial'] = gtk.CheckButton()
    x['trivial'].set_label('All premises of this rule that meet the conditions shall be considered trivial.')
    x['trivialrule'] = gtk.CheckButton()
    x['trivialrule'].set_label('The application of this rule shall be considered trivial.')
    x['x16'] = gtk.Frame()
    x['x16'].set_label('Premises:')
    x['x17'] = gtk.Grid()
    x['x17'].set_column_homogeneous(False)
    x['x17'].set_row_homogeneous(False)
    x['x18'] = gtk.ScrolledWindow()
    x['lp'] = gtk.ListStore(int,str,str,str,str)
    x['tp'] = gtk.TreeView(x['lp'])
    x['tp'].set_property('headers-visible',True)
    x['tp.selection'] = x['tp'].get_selection()
    headername = '#'
    x['tp'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=0))
    headername = 'Pattern Description'
    x['tp'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=1))
    headername = './.'
    x['tp'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=2))
    headername = 'case'
    x['tp'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=3))
    headername = 'Regular Expression'
    x['tp'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=4))
    x['x19'] = gtk.Button()
    x['x19'].set_label('New')
    x['x20'] = gtk.Button()
    x['x20'].set_label('Edit')
    x['x21'] = gtk.Button()
    x['x21'].set_label('Copy')
    x['x22'] = gtk.Button()
    x['x22'].set_label('Paste')
    x['x23'] = gtk.Button()
    x['x23'].set_label('Cut')
    x['x24'] = gtk.Frame()
    x['x24'].set_label('Conditions:')
    x['x25'] = gtk.Grid()
    x['x25'].set_column_homogeneous(False)
    x['x25'].set_row_homogeneous(False)
    x['x26'] = gtk.ScrolledWindow()
    x['lcd'] = gtk.ListStore(int,str,str)
    x['tcd'] = gtk.TreeView(x['lcd'])
    x['tcd'].set_property('headers-visible',True)
    x['tcd.selection'] = x['tcd'].get_selection()
    headername = '#'
    x['tcd'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=0))
    headername = 'Good?'
    x['tcd'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=1))
    headername = 'Expression'
    x['tcd'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=2))
    x['x27'] = gtk.Button()
    x['x27'].set_label('New')
    x['x28'] = gtk.Button()
    x['x28'].set_label('Edit')
    x['x29'] = gtk.Button()
    x['x29'].set_label('Copy')
    x['x30'] = gtk.Button()
    x['x30'].set_label('Paste')
    x['x31'] = gtk.Button()
    x['x31'].set_label('Cut')
    x['x32'] = gtk.Frame()
    x['x32'].set_label('Conclusions:')
    x['x33'] = gtk.Grid()
    x['x33'].set_column_homogeneous(False)
    x['x33'].set_row_homogeneous(False)
    x['x34'] = gtk.ScrolledWindow()
    x['lcc'] = gtk.ListStore(int,str)
    x['tcc'] = gtk.TreeView(x['lcc'])
    x['tcc'].set_property('headers-visible',True)
    x['tcc.selection'] = x['tcc'].get_selection()
    headername = '#'
    x['tcc'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=0))
    headername = 'Inferred Pattern'
    x['tcc'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=1))
    x['x35'] = gtk.Button()
    x['x35'].set_label('New')
    x['x36'] = gtk.Button()
    x['x36'].set_label('Edit')
    x['x37'] = gtk.Button()
    x['x37'].set_label('Copy')
    x['x38'] = gtk.Button()
    x['x38'].set_label('Paste')
    x['x39'] = gtk.Button()
    x['x39'].set_label('Cut')
    
    x['g0'].set_hexpand(True)
    x['dlg.contents'].add(x['g0'])
    x['x15'].set_hexpand(False)
    x['x15'].set_vexpand(False)
    x['g0'].attach(x['x15'],0,0,2,1)
    x['desc'].set_hexpand(True)
    x['desc'].set_vexpand(False)
    x['g0'].attach(x['desc'],2,0,9,1)
    x['justified'].set_hexpand(False)
    x['justified'].set_vexpand(False)
    x['g0'].attach(x['justified'],2,1,10,1)
    x['trivial'].set_hexpand(False)
    x['trivial'].set_vexpand(False)
    x['g0'].attach(x['trivial'],2,2,10,1)
    x['trivialrule'].set_hexpand(False)
    x['trivialrule'].set_vexpand(False)
    x['g0'].attach(x['trivialrule'],2,3,10,1)
    x['x16'].set_hexpand(True)
    x['x16'].set_vexpand(True)
    x['g0'].attach(x['x16'],0,4,11,8)
    x['x16'].add(x['x17'])
    x['x18'].set_size_request(600, 150)
    x['x18'].set_hexpand(True)
    x['x18'].set_vexpand(True)
    x['x17'].attach(x['x18'],0,0,6,5)
    x['x18'].add(x['tp'])
    x['x17'].attach(x['x19'],7,0,1,1)
    x['x17'].attach(x['x20'],7,1,1,1)
    x['x17'].attach(x['x21'],7,2,1,1)
    x['x17'].attach(x['x22'],7,3,1,1)
    x['x17'].attach(x['x23'],7,4,1,1)
    x['x24'].set_hexpand(True)
    x['x24'].set_vexpand(True)
    x['g0'].attach(x['x24'],0,12,11,8)
    x['x24'].add(x['x25'])
    x['x26'].set_size_request(600, 150)
    x['x26'].set_hexpand(True)
    x['x26'].set_vexpand(True)
    x['x25'].attach(x['x26'],0,0,6,5)
    x['x26'].add(x['tcd'])
    x['x25'].attach(x['x27'],7,0,1,1)
    x['x25'].attach(x['x28'],7,1,1,1)
    x['x25'].attach(x['x29'],7,2,1,1)
    x['x25'].attach(x['x30'],7,3,1,1)
    x['x25'].attach(x['x31'],7,4,1,1)
    x['x32'].set_hexpand(True)
    x['x32'].set_vexpand(True)
    x['g0'].attach(x['x32'],0,20,11,8)
    x['x32'].add(x['x33'])
    x['x34'].set_size_request(600, 150)
    x['x34'].set_hexpand(True)
    x['x34'].set_vexpand(True)
    x['x33'].attach(x['x34'],0,0,6,5)
    x['x34'].add(x['tcc'])
    x['x33'].attach(x['x35'],7,0,1,1)
    x['x33'].attach(x['x36'],7,1,1,1)
    x['x33'].attach(x['x37'],7,2,1,1)
    x['x33'].attach(x['x38'],7,3,1,1)
    x['x33'].attach(x['x39'],7,4,1,1)
    
    rule_dlg_connect(x)
    rule_dlg_show(x)
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    return x
    # End of automatically generated code section. Do not edit manually.


def rule_dlg_show(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Mon 10 Nov 2014 02:39:06 PM CET
    x['dlg'].show_all()
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def rule_dlg_connect(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Mon 10 Nov 2014 02:39:06 PM CET
    x['x19'].connect('clicked',lambda *t,x=x:rd_new(x,*t))
    x['x20'].connect('clicked',lambda *t,x=x:rd_edit(x,*t))
    x['x21'].connect('clicked',lambda *t,x=x:rd_copy(x,*t))
    x['x22'].connect('clicked',lambda *t,x=x:rd_paste(x,*t))
    x['x23'].connect('clicked',lambda *t,x=x:rd_cut(x,*t))
    x['x27'].connect('clicked',lambda *t,x=x:rdc_new(x,*t))
    x['x28'].connect('clicked',lambda *t,x=x:rdc_edit(x,*t))
    x['x29'].connect('clicked',lambda *t,x=x:rdc_copy(x,*t))
    x['x30'].connect('clicked',lambda *t,x=x:rdc_paste(x,*t))
    x['x31'].connect('clicked',lambda *t,x=x:rdc_cut(x,*t))
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def fns_dlg_build(x=None,parent=None): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 16 Sep 2014 11:36:12 AM CEST
    if x == None:
        x = {}
    
    btns = []
    btns.append(gtk.STOCK_OK)
    btns.append(gtk.ResponseType.OK)
    x['dlg'] = gtk.Dialog(parent=parent,buttons=tuple(btns))
    x['dlg'].set_title('Token Maps....')
    x['dlg.contents'] = x['dlg'].get_content_area()
    
    
    fns_dlg_connect(x)
    fns_dlg_show(x)
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    return x
    # End of automatically generated code section. Do not edit manually.


def fns_dlg_show(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 16 Sep 2014 11:36:12 AM CEST
    x['dlg'].show_all()
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def fns_dlg_connect(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 16 Sep 2014 11:36:12 AM CEST
    pass
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def map_dlg_build(x=None,parent=None): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 07 Oct 2014 02:12:37 PM CEST
    if x == None:
        x = {}
    
    btns = []
    btns.append(gtk.STOCK_CANCEL)
    btns.append(gtk.ResponseType.CANCEL)
    btns.append(gtk.STOCK_OK)
    btns.append(gtk.ResponseType.OK)
    x['dlg'] = gtk.Dialog(parent=parent,buttons=tuple(btns))
    x['dlg'].set_title('Token Map Editor')
    x['dlg.contents'] = x['dlg'].get_content_area()
    x['g0'] = gtk.Grid()
    x['g0'].set_column_homogeneous(False)
    x['g0'].set_row_homogeneous(False)
    x['x42'] = gtk.Label()
    x['x42'].set_text('Map Description:')
    x['name'] = gtk.Entry()
    x['name'].set_text('')
    x['x43'] = gtk.ScrolledWindow()
    x['l'] = gtk.ListStore(int,str,str,str)
    x['t'] = gtk.TreeView(x['l'])
    x['t'].set_property('headers-visible',True)
    x['t.selection'] = x['t'].get_selection()
    headername = '#'
    x['t'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=0))
    headername = 'RegExp?'
    x['t'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=1))
    headername = 'Input Pattern/Token'
    x['t'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=2))
    headername = 'Output Pattern/Token'
    x['t'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=3))
    x['x44'] = gtk.Button()
    x['x44'].set_label('New')
    x['x45'] = gtk.Button()
    x['x45'].set_label('Edit')
    x['x46'] = gtk.Button()
    x['x46'].set_label('Copy')
    x['x47'] = gtk.Button()
    x['x47'].set_label('Paste')
    x['x48'] = gtk.Button()
    x['x48'].set_label('Cut')
    x['x49'] = gtk.Button()
    x['x49'].set_label('Move Up')
    x['x50'] = gtk.Button()
    x['x50'].set_label('Move Down')
    x['x51'] = gtk.Frame()
    x['x51'].set_label('Preview...')
    x['x52'] = gtk.Grid()
    x['x52'].set_column_homogeneous(False)
    x['x52'].set_row_homogeneous(False)
    x['x53'] = gtk.Label()
    x['x53'].set_text('Input:')
    x['input'] = gtk.Entry()
    x['input'].set_text('')
    x['x54'] = gtk.Button()
    x['x54'].set_label('Maps to:')
    x['output'] = gtk.Entry()
    x['output'].set_text('')
    
    x['g0'].set_hexpand(True)
    x['dlg.contents'].add(x['g0'])
    x['x42'].set_hexpand(False)
    x['x42'].set_vexpand(False)
    x['g0'].attach(x['x42'],0,-1,1,1)
    x['name'].set_hexpand(True)
    x['name'].set_vexpand(False)
    x['g0'].attach(x['name'],1,-1,7,1)
    x['x43'].set_size_request(600, 350)
    x['x43'].set_hexpand(True)
    x['x43'].set_vexpand(True)
    x['g0'].attach(x['x43'],0,0,6,7)
    x['x43'].add(x['t'])
    x['g0'].attach(x['x44'],7,0,1,1)
    x['g0'].attach(x['x45'],7,1,1,1)
    x['g0'].attach(x['x46'],7,2,1,1)
    x['g0'].attach(x['x47'],7,3,1,1)
    x['g0'].attach(x['x48'],7,4,1,1)
    x['g0'].attach(x['x49'],7,5,1,1)
    x['g0'].attach(x['x50'],7,6,1,1)
    x['x51'].set_hexpand(True)
    x['x51'].set_vexpand(False)
    x['g0'].attach(x['x51'],0,8,8,3)
    x['x51'].add(x['x52'])
    x['x53'].set_hexpand(False)
    x['x53'].set_vexpand(False)
    x['x52'].attach(x['x53'],0,0,1,1)
    x['input'].set_hexpand(True)
    x['input'].set_vexpand(False)
    x['x52'].attach(x['input'],1,0,4,1)
    x['x54'].set_hexpand(False)
    x['x54'].set_vexpand(False)
    x['x52'].attach(x['x54'],0,1,1,1)
    x['output'].set_hexpand(True)
    x['output'].set_vexpand(False)
    x['x52'].attach(x['output'],1,1,4,1)
    
    map_dlg_connect(x)
    map_dlg_show(x)
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    return x
    # End of automatically generated code section. Do not edit manually.


def map_dlg_show(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 07 Oct 2014 02:12:37 PM CEST
    x['dlg'].show_all()
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def map_dlg_connect(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 07 Oct 2014 02:12:37 PM CEST
    x['x44'].connect('clicked',lambda *t,x=x:map_dlg_new(x,*t))
    x['x45'].connect('clicked',lambda *t,x=x:map_dlg_edit(x,*t))
    x['x46'].connect('clicked',lambda *t,x=x:map_dlg_copy(x,*t))
    x['x47'].connect('clicked',lambda *t,x=x:map_dlg_paste(x,*t))
    x['x48'].connect('clicked',lambda *t,x=x:map_dlg_cut(x,*t))
    x['x49'].connect('clicked',lambda *t,x=x:map_dlg_moveup(x,*t))
    x['x50'].connect('clicked',lambda *t,x=x:map_dlg_movedown(x,*t))
    x['x54'].connect('clicked',lambda *t,x=x:map_dlg_preview(x,*t))
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def map_line_dlg_build(x=None,parent=None): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 07 Oct 2014 02:12:37 PM CEST
    if x == None:
        x = {}
    
    btns = []
    btns.append(gtk.STOCK_CANCEL)
    btns.append(gtk.ResponseType.CANCEL)
    btns.append(gtk.STOCK_OK)
    btns.append(gtk.ResponseType.OK)
    x['dlg'] = gtk.Dialog(parent=parent,buttons=tuple(btns))
    x['dlg'].set_title('Edit Token Map Pattern...')
    x['dlg.contents'] = x['dlg'].get_content_area()
    x['g0'] = gtk.Grid()
    x['g0'].set_column_homogeneous(False)
    x['g0'].set_row_homogeneous(False)
    x['regexp'] = gtk.CheckButton()
    x['regexp'].set_label('Use Regular-Expressions-Pattern')
    x['x55'] = gtk.Label()
    x['x55'].set_text('Input Pattern/Token:')
    x['input'] = gtk.Entry()
    x['input'].set_text('')
    x['x56'] = gtk.Label()
    x['x56'].set_text('Output Pattern/Token:')
    x['output'] = gtk.Entry()
    x['output'].set_text('')
    
    x['g0'].set_hexpand(True)
    x['dlg.contents'].add(x['g0'])
    x['g0'].attach(x['regexp'],0,-1,5,1)
    x['x55'].set_hexpand(False)
    x['x55'].set_vexpand(False)
    x['g0'].attach(x['x55'],0,0,1,1)
    x['input'].set_hexpand(True)
    x['input'].set_vexpand(False)
    x['g0'].attach(x['input'],1,0,4,1)
    x['x56'].set_hexpand(False)
    x['x56'].set_vexpand(False)
    x['g0'].attach(x['x56'],0,1,1,1)
    x['output'].set_hexpand(True)
    x['output'].set_vexpand(False)
    x['g0'].attach(x['output'],1,1,4,1)
    
    map_line_dlg_connect(x)
    map_line_dlg_show(x)
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    return x
    # End of automatically generated code section. Do not edit manually.


def map_line_dlg_show(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 07 Oct 2014 02:12:37 PM CEST
    x['dlg'].show_all()
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def map_line_dlg_connect(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 07 Oct 2014 02:12:37 PM CEST
    pass
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def map_edit_dialog_build(x=None,parent=None): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 07 Oct 2014 02:12:37 PM CEST
    if x == None:
        x = {}
    
    btns = []
    btns.append(gtk.STOCK_CANCEL)
    btns.append(gtk.ResponseType.CANCEL)
    btns.append(gtk.STOCK_OK)
    btns.append(gtk.ResponseType.OK)
    x['dlg'] = gtk.Dialog(parent=parent,buttons=tuple(btns))
    x['dlg'].set_title('Edit Token Maps...')
    x['dlg.contents'] = x['dlg'].get_content_area()
    x['g0'] = gtk.Grid()
    x['g0'].set_column_homogeneous(False)
    x['g0'].set_row_homogeneous(False)
    x['x57'] = gtk.Button()
    x['x57'].set_label('New')
    x['x58'] = gtk.Button()
    x['x58'].set_label('Edit')
    x['x59'] = gtk.Button()
    x['x59'].set_label('Copy')
    x['x60'] = gtk.Button()
    x['x60'].set_label('Paste')
    x['x61'] = gtk.Button()
    x['x61'].set_label('Cut')
    x['x62'] = gtk.ScrolledWindow()
    x['l'] = gtk.ListStore(int,str)
    x['t'] = gtk.TreeView(x['l'])
    x['t'].set_property('headers-visible',True)
    x['t.selection'] = x['t'].get_selection()
    headername = '#'
    x['t'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=0))
    headername = 'Map Description'
    x['t'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=1))
    
    x['g0'].set_hexpand(True)
    x['dlg.contents'].add(x['g0'])
    x['g0'].attach(x['x57'],7,0,1,1)
    x['g0'].attach(x['x58'],7,1,1,1)
    x['g0'].attach(x['x59'],7,2,1,1)
    x['g0'].attach(x['x60'],7,3,1,1)
    x['g0'].attach(x['x61'],7,4,1,1)
    x['x62'].set_size_request(600, 350)
    x['x62'].set_hexpand(True)
    x['x62'].set_vexpand(True)
    x['g0'].attach(x['x62'],0,-1,6,7)
    x['x62'].add(x['t'])
    
    map_edit_dialog_connect(x)
    map_edit_dialog_show(x)
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    return x
    # End of automatically generated code section. Do not edit manually.


def map_edit_dialog_show(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 07 Oct 2014 02:12:37 PM CEST
    x['dlg'].show_all()
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def map_edit_dialog_connect(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 07 Oct 2014 02:12:37 PM CEST
    x['x57'].connect('clicked',lambda *t,x=x:map_edit_dialog_new(x,*t))
    x['x58'].connect('clicked',lambda *t,x=x:map_edit_dialog_edit(x,*t))
    x['x59'].connect('clicked',lambda *t,x=x:map_edit_dialog_copy(x,*t))
    x['x60'].connect('clicked',lambda *t,x=x:map_edit_dialog_paste(x,*t))
    x['x61'].connect('clicked',lambda *t,x=x:map_edit_dialog_cut(x,*t))
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def map_selection_dialog_build(x=None,parent=None): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 07 Oct 2014 02:12:37 PM CEST
    if x == None:
        x = {}
    
    btns = []
    btns.append(gtk.STOCK_CANCEL)
    btns.append(gtk.ResponseType.CANCEL)
    btns.append(gtk.STOCK_OK)
    btns.append(gtk.ResponseType.OK)
    x['dlg'] = gtk.Dialog(parent=parent,buttons=tuple(btns))
    x['dlg'].set_title('Select Token Map...')
    x['dlg.contents'] = x['dlg'].get_content_area()
    x['g0'] = gtk.Grid()
    x['g0'].set_column_homogeneous(False)
    x['g0'].set_row_homogeneous(False)
    x['x63'] = gtk.Button()
    x['x63'].set_label('Edit...')
    x['x64'] = gtk.ScrolledWindow()
    x['l'] = gtk.ListStore(int,str)
    x['t'] = gtk.TreeView(x['l'])
    x['t'].set_property('headers-visible',True)
    x['t.selection'] = x['t'].get_selection()
    headername = '#'
    x['t'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=0))
    headername = 'Map Description'
    x['t'].append_column(gtk.TreeViewColumn(headername,gtk.CellRendererText(),text=1))
    
    x['g0'].set_hexpand(True)
    x['dlg.contents'].add(x['g0'])
    x['x63'].set_hexpand(False)
    x['x63'].set_vexpand(False)
    x['g0'].attach(x['x63'],5,-1,1,1)
    x['x64'].set_size_request(600, 350)
    x['x64'].set_hexpand(True)
    x['x64'].set_vexpand(True)
    x['g0'].attach(x['x64'],0,0,6,7)
    x['x64'].add(x['t'])
    
    map_selection_dialog_connect(x)
    map_selection_dialog_show(x)
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    return x
    # End of automatically generated code section. Do not edit manually.


def map_selection_dialog_show(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 07 Oct 2014 02:12:37 PM CEST
    x['dlg'].show_all()
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def map_selection_dialog_connect(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 07 Oct 2014 02:12:37 PM CEST
    pass
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def condition_editor_dlg_build(x=None,parent=None): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Mon 10 Nov 2014 01:36:23 PM CET
    if x == None:
        x = {}
    
    btns = []
    btns.append(gtk.STOCK_CANCEL)
    btns.append(gtk.ResponseType.CANCEL)
    btns.append(gtk.STOCK_OK)
    btns.append(gtk.ResponseType.OK)
    x['dlg'] = gtk.Dialog(parent=parent,buttons=tuple(btns))
    x['dlg'].set_title('Condition Editor')
    x['dlg.contents'] = x['dlg'].get_content_area()
    x['g0'] = gtk.Grid()
    x['g0'].set_column_homogeneous(False)
    x['g0'].set_row_homogeneous(False)
    x['x40'] = gtk.Label()
    x['x40'].set_text('Description:')
    x['desc'] = gtk.Entry()
    x['desc'].set_text('')
    x['slash'] = gtk.CheckButton()
    x['slash'].set_label('slash-mode')
    x['case'] = gtk.CheckButton()
    x['case'].set_label('case-sensitive mode')
    x['x41'] = gtk.Label()
    x['x41'].set_text('Expression:')
    x['cond'] = gtk.Entry()
    x['cond'].set_text('')
    
    x['g0'].set_hexpand(True)
    x['dlg.contents'].add(x['g0'])
    x['x40'].set_hexpand(False)
    x['x40'].set_vexpand(False)
    x['g0'].attach(x['x40'],0,0,1,1)
    x['desc'].set_hexpand(True)
    x['desc'].set_vexpand(False)
    x['g0'].attach(x['desc'],1,0,10,1)
    x['slash'].set_hexpand(False)
    x['slash'].set_vexpand(False)
    x['g0'].attach(x['slash'],1,3,2,1)
    x['case'].set_hexpand(False)
    x['case'].set_vexpand(False)
    x['g0'].attach(x['case'],3,3,3,1)
    x['x41'].set_hexpand(False)
    x['x41'].set_vexpand(False)
    x['g0'].attach(x['x41'],0,3,1,1)
    x['cond'].set_hexpand(True)
    x['cond'].set_vexpand(False)
    x['g0'].attach(x['cond'],0,4,11,1)
    
    condition_editor_dlg_connect(x)
    condition_editor_dlg_show(x)
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    return x
    # End of automatically generated code section. Do not edit manually.


def condition_editor_dlg_show(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Mon 10 Nov 2014 01:36:23 PM CET
    x['dlg'].show_all()
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def condition_editor_dlg_connect(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Mon 10 Nov 2014 01:36:23 PM CET
    pass
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def conditions_dlg_build(x=None,parent=None): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 07 Oct 2014 12:52:56 PM CEST
    if x == None:
        x = {}
    
    btns = []
    btns.append(gtk.STOCK_CANCEL)
    btns.append(gtk.ResponseType.CANCEL)
    btns.append(gtk.STOCK_OK)
    btns.append(gtk.ResponseType.OK)
    x['dlg'] = gtk.Dialog(parent=parent,buttons=tuple(btns))
    x['dlg'].set_title('Edit Premise Condition...')
    x['dlg.contents'] = x['dlg'].get_content_area()
    x['g0'] = gtk.Grid()
    x['g0'].set_column_homogeneous(False)
    x['g0'].set_row_homogeneous(False)
    
    x['g0'].set_hexpand(True)
    x['dlg.contents'].add(x['g0'])
    
    conditions_dlg_connect(x)
    conditions_dlg_show(x)
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    return x
    # End of automatically generated code section. Do not edit manually.


def conditions_dlg_show(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 07 Oct 2014 12:52:56 PM CEST
    x['dlg'].show_all()
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.


def conditions_dlg_connect(x): # do not edit this line.
    # Automatically generated code. Do not edit manually.
    # Generated Tue 07 Oct 2014 12:52:56 PM CEST
    pass
    # Begin of manually editable code section. It is safe to edit below.
    
    # End of manually editable code section. It is NOT safe to edit below.
    # End of automatically generated code section. Do not edit manually.

# ALL GENERATED CODE GOES ABOVE HERE

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

def short_id():
    return uuid.uuid4().hex.upper()[0:6]

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

def matching_block(blocks, c):
    if len(c) != len(blocks):
        return False
    for i in range(len(blocks)):
        if c[i] == "*":
            continue #wildcard
        elif not c[i] in blocks[i]:
            return False
    return True

def get_conditions(blocks, dep):
    conditions = []
    x = [q.strip() for q in dep.split("/")]
    asterisk_positions = [i for i in range(len(x)) if x[i] == "**"]
    asterisk_positions.sort(reverse=True)
    if asterisk_positions:
        candidates = [x]
        for q in asterisk_positions:
            new_candidates = []
            for i in range(len(blocks)):
                for x in candidates:
                    new_candidates.append(x[:q]+["*" for j in range(i)]+x[q+1:])
            candidates = new_candidates
    else:
        candidates = [x]
    BLOCKS = [[x.upper() for x in blocks[i]] for i in range(len(blocks))]
    for x in candidates:
        if len(x) < len(blocks):
            for i in range(len(x),len(blocks)):
                x.append("")
        if len(x) == len(blocks):
            X = [q.upper() for q in x]
            if matching_block(BLOCKS,X):
                conditions.append(tuple(X))

    return conditions

def format_string(x,space):
    s = ""
    for i in range(len(x)):
        s += x[i]
        if space[i]:
            s += " "
    return s

def build_template(examples, constraints,static="",excluded=False):
    ex = [x for x in filter(lambda x: x.strip()!="",examples.split("\n"))]
    dep = [x for x in filter(lambda x: x.strip()!="",constraints.split("\n"))]
    t = {}
    if type(excluded) == str:
        excluded = excluded == "True"
    t["excluded"] = excluded
    t["blocks"] = []
    t["positive"] = []
    t["negative"] = []
    t["ignored_examples"] = []
    t["used_examples"] = []
    t["ignored_constraints"] = []
    t["used_constraints"] = []

    nbrs = {}
    for x in ex:
        i = len(x.split("/"))
        nbrs[i] = nbrs.get(i,0)+1

    if nbrs:
        nbr_blocks = [x for x in nbrs.keys()][0]
        for x in nbrs.keys():
            if nbrs[x] > nbrs[nbr_blocks]:
                nbr_blocks = x
    else:
        nbr_blocks = 0
    t["blocks"] = [[] for x in range(nbr_blocks)]
    t["conditions"] = [{} for x in range(nbr_blocks)]
    t["space"] = [False for x in range(nbr_blocks)]
    t["interactive"] = [True for x in range(nbr_blocks)]

    for nbr in static.split(","):
        try:
            i = int(nbr.strip())
            if  0 < i <= nbr_blocks:
                t["interactive"][i-1] = False
        except:
            pass
    for x in ex:
        parts = x.split("/")
        if len(parts) != nbr_blocks:
            t["ignored_examples"].append((x,"ex. has %d slashes (/), but the others have %d!" % 
                        (len(parts)-1,nbr_blocks-1)))
        else:
            t["used_examples"].append(x)
            for i in range(nbr_blocks):
                part = t["blocks"][i]
                p = parts[i].strip()
                if not p in part:
                    part.append(p)
                p = parts[i]
                if len(p) and p[0].isspace():
                    if i > 0:
                        t["space"][i-1] = True
                if len(p) and p[-1].isspace():
                    if i < nbr_blocks-1:
                        t["space"][i] = True

    for i in range(nbr_blocks):
        t["blocks"][i].sort()


    for x in dep:
        kind = "positive"
        old_x = x
        if x.startswith("+"):
            x = x[1:].strip()
        elif x.startswith("-"):
            kind = "negative"
            x = x[1:].strip()
        else:
            x = x.strip()
        applies = get_conditions(t["blocks"],x)
        t[kind].extend(applies)
        if len(applies) == 0:
            t["ignored_constraints"].append((old_x,"pat. doesn't match any sentence!"))
        else:
            t["used_constraints"].append(old_x)

    prepare_conditions(t)

    t["ignored_examples"].sort()
    t["ignored_constraints"].sort()
    t["used_examples"].sort()
    t["used_constraints"].sort()
    return t

def clen(x):
    q = 0
    for p in x:
        if p != "*":
            q += 1
    return q

def prepare_conditions(t):
    cond = [{} for i in range(len(t["blocks"]))]
    ordered = [(True, x) for x in t["positive"]]+[(False,x) for x in t["negative"]]
    ordered.sort(key=cmp_to_key(lambda l,r: 2*clen(l[1]) + (0 if l[0] else 1) - 2*clen(r[1]) - (0 if r[0] else 1) ),
            reverse=True)
    for positive, c in ordered:
        for i in range(len(c)):
            if c[i] == "*":
                continue
            key_wise = cond[i].get(c[i],[])
            key_wise.append((positive,c))
            cond[i][c[i]] = key_wise
    t["conditions"] = cond

    return t

def matches_condition(x,c):
    for i in range(len(c)):
        if c[i] == "*":
            continue
        else:
            if c[i] != x[i]:
                return False
    return True

def valid_patterns(t):
    blocks = t["blocks"]
    buildup = [[]]
    for i in range(len(blocks)):
        new_buildup = []
        for q in blocks[i]:
            for p in buildup:
                new_buildup.append(p+[q])
        buildup = new_buildup
    valid = []
    check = [i for i in range(len(blocks)) if t["conditions"][i]!={}]
    
    for x in buildup:
        X = [c.upper() for c in x]
        okay = True
        for i in check:          
            cond = t["conditions"][i]
            if X[i] in cond:
                okay = False
                for positive, c in cond[X[i]]:
                    if matches_condition(X,c):
                        okay = positive
                        break
            else:
                okay = False
            if okay == False:
                break
        if okay:
            valid.append(tuple(x))

    valid.sort()

    t["valid"] = valid
    t["formatted"] = [format_string(x, t["space"]) for x in valid]


    return valid


def edit_template(t=None,parent=None):
    if t == None:
        t = build_template("","","")
    
    print("Edit:",t)

    dlg = edit_sentence_templates_build()
    if parent:
        dlg["dlg"].set_transient_for(parent)

    dlg["nouser"].set_active(t["excluded"])

    patterns = "\n".join([x for x,e in t["ignored_examples"]])
    errorlen = len(patterns)
    if len(patterns):
        patterns += "\n\n"
    patterns += "\n".join(t["used_examples"])

    dlg["exs"].get_buffer().set_text(patterns)

    patterns = "\n".join([x for x,e in t["ignored_constraints"]])
    errorlen = len(patterns)
    if len(patterns):
        patterns += "\n\n"
    patterns += "\n".join(t["used_constraints"])

    dlg["cons"].get_buffer().set_text(patterns)

    static = ""
    for i in range(len(t["interactive"])):
        if t["interactive"][i] == False:
            if static:
                static += ", "
            static += str(i+1)

    dlg["noninter"].set_text(static)

    retval = dlg["dlg"].run()

    static = dlg["noninter"].get_text()

    if retval == gtk.ResponseType.CANCEL:
        dlg["dlg"].destroy()
        return t

    q = build_template(dlg["exs"].get_buffer().get_text(dlg["exs"].get_buffer().get_start_iter(),
                                                       dlg["exs"].get_buffer().get_end_iter(),False),
                       dlg["cons"].get_buffer().get_text(dlg["cons"].get_buffer().get_start_iter(),
                                                       dlg["cons"].get_buffer().get_end_iter(),False),
                       static, dlg["nouser"].get_active())
    dlg["dlg"].destroy()



    return q

def preview_template1(dlg,*x):
    modal = template_preview_dlg_build(parent=dlg["dlg"])
    box = modal["h"]

    q = build_template(dlg["exs"].get_buffer().get_text(dlg["exs"].get_buffer().get_start_iter(),
                                                       dlg["exs"].get_buffer().get_end_iter(),False),"")
    for block,space in zip(q["blocks"],q["space"]):
        combo = gtk.ComboBoxText()
        combo.set_entry_text_column(0)

        for b in block:
            combo.append_text(b)

        combo.set_active(0)

        box.pack_start(combo, False, False, 0)
        if space:
            lbl = gtk.Label()
            lbl.set_text(" ")
            box.pack_start(lbl, False, False, 0)

    box.show_all()

    patterns = "\n".join([x for x,e in q["ignored_examples"]])
    errorlen = len(patterns)
    if len(patterns):
        patterns += "\n\n"
    patterns += "\n".join(q["used_examples"])
    dlg["exs"].get_buffer().set_text(patterns)

    modal["dlg"].run()
    modal["dlg"].destroy()

    if errorlen:
        ignored = ignored_exs_dlg_build(parent=dlg["dlg"])
        for x,e in q["ignored_examples"]:
            ignored["l0"].append([x,e])

        ignored["dlg"].run()
        ignored["dlg"].destroy()

def preview_template2(dlg,*x):
    modal = list_preview_dlg_build(parent=dlg["dlg"])
    q = build_template(dlg["exs"].get_buffer().get_text(dlg["exs"].get_buffer().get_start_iter(),
                                                       dlg["exs"].get_buffer().get_end_iter(),False),
                       dlg["cons"].get_buffer().get_text(dlg["cons"].get_buffer().get_start_iter(),
                                                       dlg["cons"].get_buffer().get_end_iter(),False))

    patterns = "\n".join([x for x,e in q["ignored_constraints"]])
    errorlen = len(patterns)
    if len(patterns):
        patterns += "\n\n"
    patterns += "\n".join(q["used_constraints"])
    dlg["cons"].get_buffer().set_text(patterns)

    valid_patterns(q)   

    for f in q["formatted"]:
        modal["l0"].append([f])

    modal["dlg"].run()
    modal["dlg"].destroy()
    


    if errorlen:
        ignored = ignored_exs_dlg2_build(parent=dlg["dlg"])
        for x,e in q["ignored_constraints"]:
            ignored["l0"].append([x,e])

        ignored["dlg"].run()
        ignored["dlg"].destroy()

def preview_template3(dlg, *x):
    static = dlg["noninter"].get_text()
    q = build_template(dlg["exs"].get_buffer().get_text(dlg["exs"].get_buffer().get_start_iter(),
                                                       dlg["exs"].get_buffer().get_end_iter(),False),
                       dlg["cons"].get_buffer().get_text(dlg["cons"].get_buffer().get_start_iter(),
                                                       dlg["cons"].get_buffer().get_end_iter(),False),
                       static)
    dlg = ui_demo_dlg_build(parent=dlg["dlg"])
    ui = build_template_ui(q)
    dlg["v"].add(ui["box"])
    ui["box"].show_all()
    dlg["dlg"].run()
    dlg["dlg"].destroy()

def hamming(x,y):
    d = 0
    for i in range(len(x)):
        if x[i] != y[i]:
            d += 1
    return d

def template_ui_combo_changed(i,t,ui):
    combo = ui[i]
    n = combo.get_active()
    old_val = ui["current"][i]
    new_val = t["blocks"][i][n]
    candidate = [x for x in ui["current"]]
    candidate[i] = new_val
    ctuple = tuple(candidate)
    if ctuple in t["valid"]:
        ui["current"] = candidate
    else:
        distance = [(hamming(ctuple,x),x) for x in t["valid"] if x[i] == new_val]
        if distance:
            distance.sort()
            closest = distance[0][1]
            for j in range(len(t["blocks"])):
                if t["interactive"][j]:
                    ui[j].set_active(t["blocks"][j].index(closest[j]))
                else:
                    ui[j].set_text(closest[j])
            ui["current"] = closest
        else:
            #IMPOSSIBLE choice! reset to old value!
            ui[i].set_active(t["blocks"][i].index(old_val))


def build_template_ui(t):
    default = valid_patterns(t)
    if len(default):
        default = default[0]
    else:
        default = None

    ui = {}
    ui["current"] = []
    ui["box"] = gtk.HBox()
    box = ui["box"]
    counter = 0
    for block,space,user in zip(t["blocks"],t["space"],t["interactive"]):
        el = None
        if user:
            el = gtk.ComboBoxText()
            el.set_entry_text_column(0)
            for x in block:
                el.append_text(x)
            if default:
                el.set_active(block.index(default[counter]))
                ui["current"].append(default[counter])
            else:
                el.set_active(0)
                ui["current"].append(block[0])
            el.connect("changed",lambda *p,i=counter,t=t,ui=ui: template_ui_combo_changed(i,t,ui))
        else:
            el = gtk.Label()
            if default:
                el.set_text(default[counter])
                ui["current"].append(default[counter])
            else:
                el.set_text(block[0])
                ui["current"].append(block[0])

        ui[counter] = el
        box.pack_start(el,False,False,0)
        if space:
            lbl = gtk.Label()
            lbl.set_text(" ")
            box.pack_start(lbl,False,False,0)
            
        counter += 1


    return ui

def get_template_repr(t):
    if t == None:
        return ("","","",False)

    patterns = "\n".join([x for x,e in t["ignored_examples"]])
    errorlen = len(patterns)
    if len(patterns):
        patterns += "\n\n"
    patterns += "\n".join(t["used_examples"])

    ex = patterns

    patterns = "\n".join([x for x,e in t["ignored_constraints"]])
    errorlen = len(patterns)
    if len(patterns):
        patterns += "\n\n"
    patterns += "\n".join(t["used_constraints"])

    con = patterns

    static = ""
    for i in range(len(t["interactive"])):
        if t["interactive"][i] == False:
            if static:
                static += ", "
            static += str(i+1)

    return (ex,con,static,t["excluded"])

def set_clipboard(s):
    c = gtk.Clipboard.get(gdk.SELECTION_CLIPBOARD)
    c.set_text(str(s),-1)

def get_clipboard():
    c = gtk.Clipboard.get(gdk.SELECTION_CLIPBOARD)
    return c.wait_for_text()


def task_valid(x):
    x["valid"] = []
    x["formatted"] = []
    for t in x["templates"]:
        valid_patterns(t)   
        x["valid"].extend(t["valid"])
        x["formatted"].extend(t["formatted"])

    return x["valid"]

def build_task(x=None):
    if x == None:
        x = {}

    x["templates"] = []
    x["valid"] = []
    x["formatted"] = []

    x["maps"] = []
    x["rules"] = []

    return x

def clt_new_template(dlg,*x):
    dlg["dlg"].hide()
    new = edit_template(build_template("","",""),dlg["dlg"])
    tr = get_template_repr(new)
    if tr == ("","","",False) or tr == ("","","",True):
        dlg["dlg"].response(1000)
        return
    valid_patterns(new)
    dlg["t"]["templates"].append(new)

    dlg["dlg"].response(1000)

def clt_edit_template(dlg,*x):
    try:
       i = int(str(dlg["tp"].get_selection().get_selected_rows()[1][0]))
    except:
        return
    dlg["dlg"].hide()
    new = edit_template(dlg["t"]["templates"][i],dlg["dlg"])
    tr = get_template_repr(new)
    if tr == ("","","",False) or tr == ("","","",True):
        dlg["dlg"].response(1000)
        dlg["t"]["templates"].pop(i)
        return

    valid_patterns(new)
    dlg["t"]["templates"][i] = new

    dlg["dlg"].response(1000)

def clt_copy_template(dlg,*x):
    try:
       i = int(str(dlg["tp"].get_selection().get_selected_rows()[1][0]))
    except:
        return
    new = dlg["t"]["templates"][i]
    tr = get_template_repr(new)
    if tr == ("","","",False) or tr == ("","","",True):
        return
    set_clipboard("\n###\n".join(map(str,get_template_repr(new))))

def clt_cut_template(dlg,*x):
    try:
       i = int(str(dlg["tp"].get_selection().get_selected_rows()[1][0]))
    except:
        return
    new = dlg["t"]["templates"][i]
    tr = get_template_repr(new)
    if tr == ("","","",False):
        return
    set_clipboard("\n###\n".join(map(str,get_template_repr(new))))
    dlg["t"]["templates"].pop(i)
    dlg["dlg"].hide()
    dlg["dlg"].response(1000)

def clt_paste_template(dlg,*x):
    parts = "\n".join(x.strip() for x in get_clipboard().split("\n"))
    triple = parts.split("\n###\n")
    if len(triple) != 4:
        return
    if triple == ("","","","False") or triple == ("","","","True"):
        return

    new = build_template(*triple)
    try:
       i = int(str(dlg["tp"].get_selection().get_selected_rows()[1][0]))
    except:
        i = len(dlg["t"]["templates"])
    dlg["dlg"].hide()
    valid_patterns(new)
    dlg["t"]["templates"].insert(i,new)
    dlg["dlg"].response(1000)

def clt_list_all_templates(dlg,*x):
    modal = list_preview_dlg_build(parent=dlg["dlg"])
    t = dlg["t"]

    task_valid(t)   
    sorted = t["formatted"][:]
    sorted.sort()

    for f in sorted:
        modal["l0"].append([f])

    modal["dlg"].run()
    modal["dlg"].destroy()

def clt_preview_all_templates(dlg,*x):
    modal = templates_preview_dlg_build(parent=dlg["dlg"])
    for t in dlg["t"]["templates"]:
        if t["excluded"]:
            continue
        ui = build_template_ui(t)
        btn = gtk.Button()
        btn.set_label("Add")
        ui["box"].pack_end(btn,False,False,0)
        modal["h"].add(ui["box"])
        ui["box"].show_all()

    modal["dlg"].run()
    modal["dlg"].destroy()

def edit_task(t):

    retval = 1000
    my_t = copy.deepcopy(t)
    

    while retval == 1000:
        dlg = clt_dlg_build()
        # data -> dlg

        dlg["t"] = my_t
        count = 1
        for new in my_t["templates"]:
            valid_patterns(new)
            if len(new["formatted"]):
                sample = new["formatted"][0]
            else:
                sample = "(Empty)"
            if new["excluded"]:
                user = "No"
            else:
                user = "Yes"
            dlg["lp"].append([count, user, sample])
            count += 1
        i = 0
        for rule in dlg["t"]["rules"]:
            i+=1
            dlg["lr"].append([i,"Jp" if rule["justified-premises"] else "", "Tp" if rule["trivial-premises"] else "",
                          "Tr" if rule["trivial-rule"] else "", rule["name"]])
            pass

        retval = dlg["dlg"].run()
        dlg["dlg"].destroy()
    

    if retval == gtk.ResponseType.CANCEL:
        return t



    return dlg["t"]


def build_pattern(case=False,slash=False,name="",regexp="",p=None):
    if type(case) == str:
        case = case == "True"
    if type(slash) == str:
        slash = slash == "True"
    if p==None:
        p = {}

    p["slash"] = slash
    p["case"] = case #case sensitive pattern matching?
    p["name"] = name
    p["regexp"] = regexp


    return p

def pattern_repr(p):
    return (p["case"],p["slash"],p["name"],p["regexp"])


def pattern_match(p,sentences,formatted):
    if p["slash"]:
        candidates = ["/".join(x) for x in sentences]
    else:
        candidates = formatted

    rex = p["regexp"]
    if not rex.endswith("$"):
        rex += "$"

    if not p["case"]:
        candidates = [x.upper() for x in candidates]
        r = re.compile(rex,flags=re.I)
    else:
        r = re.compile(rex)
    take_it = [r.match(x) for x in candidates]
    return [(sentences[i],formatted[i]) for i in range(len(candidates)) if take_it[i]]
    
    

def match_pattern_preview(dlg,*x):
    p2 = build_pattern(dlg["case"].get_active(),dlg["slash"].get_active(),dlg["desc"].get_text(),
            dlg["regexp"].get_text())
    dlg["lp"].clear()
    for s,f in pattern_match(p2, dlg["task"]["valid"], dlg["task"]["formatted"]):
        dlg["lp"].append([f])

def edit_pattern(p,task,parent=None):
    dlg = pattern_dlg_build(parent=parent)

    dlg["slash"].set_active(p["slash"])
    dlg["case"].set_active(p["case"])
    dlg["desc"].set_text(p["name"])
    dlg["regexp"].set_text(p["regexp"])

    task_valid(task)
    dlg["task"] = task

    retval = dlg["dlg"].run()
    if retval == gtk.ResponseType.CANCEL:
        dlg["dlg"].destroy()
        return p

    p2 = build_pattern(dlg["case"].get_active(),dlg["slash"].get_active(),dlg["desc"].get_text(),
            dlg["regexp"].get_text())

    dlg["dlg"].destroy()
    return p2

def is_true(x):
    if type(x)==str:
        return x=="True"
    else:
        return bool(x)

def build_rule(name="",justified=False,trivial_p=False,trivial_r=False,premises="",conditions="",conclusions="",r=None):
    if r == None:
        r = {}

    r["name"] = name
    r["premises"] = []
    for q in premises.split("\n######\n"):
        if q != "":
            r["premises"].append(build_pattern(*(q.split("\n###\n"))))
    r["justified-premises"] = is_true(justified)
    r["trivial-premises"] = is_true(trivial_p)
    r["trivial-rule"] = is_true(trivial_r)
    r["conditions"] = []
    for q in conditions.split("\n######\n"):
        if q != "":
            r["conditions"].append(build_condition(*(q.split("\n###\n"))))
    
    r["conclusions"] = []
    #TODO: parse conclusions
    

    return r

def rule_repr(r):
    pstr = "\n######\n".join(["\n###\n".join(map(str,pattern_repr(x))) for x in r["premises"]])
    cstr = "\n######\n".join(["\n###\n".join(map(str,condition_repr(x))) for x in r["conditions"]])
    #TODO: represent conclusions
    return r["name"],r["justified-premises"],r["trivial-premises"],r["trivial-rule"],pstr,cstr

rule_repr_length = len(rule_repr(build_rule()))


def edit_rule(r,task,parent=None):
    my_r = copy.deepcopy(r)

    retval = 1000
    while retval == 1000:
        dlg = rule_dlg_build()
        dlg["r"] = my_r
        dlg["task"] = task

        #my_r -> dlg

        i = 0
        for pat in dlg["r"]["premises"]:
            i+=1
            if pat["slash"]:
                sl = "a/b"
            else:
                sl = "a b"
            if pat["case"]:
                up = "a≠A"
            else:
                up = "a=A"
            dlg["lp"].append([i,pat["name"],sl,up,pat["regexp"]])

        dlg["trivial"].set_active(dlg["r"]["trivial-premises"])
        dlg["trivialrule"].set_active(dlg["r"]["trivial-rule"])
        dlg["justified"].set_active(dlg["r"]["justified-premises"])
        dlg["desc"].set_text(dlg["r"]["name"])
        
        retval = dlg["dlg"].run()

        dlg["r"]["trivial-premises"] = dlg["trivial"].get_active()
        dlg["r"]["trivial-rule"] = dlg["trivialrule"].get_active()
        dlg["r"]["justified-premises"] = dlg["justified"].get_active()
        dlg["r"]["name"] = dlg["desc"].get_text()

    if retval == gtk.ResponseType.CANCEL:
        dlg["dlg"].destroy()
        return r

    #dlg -> r
    dlg["dlg"].destroy()

    return dlg["r"]

def rd_new(dlg,*dontcare):
    r = dlg["r"]
    dlg["dlg"].hide()

    dlg["r"]["premises"].append(edit_pattern(build_pattern(),dlg["task"]))
    dlg["dlg"].response(1000)

def rd_edit(dlg,*dontcare):
    try:
       i = int(str(dlg["tp"].get_selection().get_selected_rows()[1][0]))
    except:
        return
    r = dlg["r"]
    dlg["dlg"].hide()

    dlg["r"]["premises"][i] = edit_pattern(dlg["r"]["premises"][i],dlg["task"])
    dlg["dlg"].response(1000)

def rd_copy(dlg,*dontcare):
    try:
       i = int(str(dlg["tp"].get_selection().get_selected_rows()[1][0]))
    except:
        return
    r = dlg["r"]
    set_clipboard("\n###\n".join(map(str,pattern_repr(r["premises"][i]))))
def rd_cut(dlg,*dontcare):
    try:
       i = int(str(dlg["tp"].get_selection().get_selected_rows()[1][0]))
    except:
        return
    r = dlg["r"]
    set_clipboard("\n###\n".join(map(str,pattern_repr(r["premises"][i]))))
    dlg["dlg"].hide()
    dlg["dlg"].response(1000)
    r["premises"].pop(i)
def rd_paste(dlg,*dontcare):
    c = get_clipboard().split("\n###\n")
    if len(c) != 4:
        return
    p = build_pattern(*c)
    try:
       i = int(str(dlg["tp"].get_selection().get_selected_rows()[1][0]))
    except:
       i = len(dlg["r"]["premises"])

    dlg["dlg"].hide()
    dlg["dlg"].response(1000)
    dlg["r"]["premises"].insert(i,p)
    

def clt_ir_new(dlg,*dontcare):
    dlg["dlg"].hide()
    rule = edit_rule(build_rule(),dlg["t"],dlg["dlg"])
    dlg["t"]["rules"].append(rule)
    dlg["dlg"].response(1000)
    
def clt_ir_edit(dlg,*dontcare):
    try:
       i = int(str(dlg["tr"].get_selection().get_selected_rows()[1][0]))
    except:
       return
    
    dlg["dlg"].hide()
    dlg["t"]["rules"][i] = edit_rule(dlg["t"]["rules"][i],dlg["t"],dlg["dlg"])
    dlg["dlg"].response(1000)

def clt_ir_copy(dlg,*dontcare):
    try:
       i = int(str(dlg["tr"].get_selection().get_selected_rows()[1][0]))
    except:
       return
    set_clipboard("\n########\n".join(map(str,rule_repr(dlg["t"]["rules"][i]))))

def clt_ir_paste(dlg,*dontcare):
    data = get_clipboard().split("\n########\n")
    if len(data) != rule_repr_length:
            return
    try:
       i = int(str(dlg["tr"].get_selection().get_selected_rows()[1][0]))
    except:
       i = len(dlg["t"]["rules"])

    dlg["dlg"].hide()
    dlg["t"]["rules"].insert(i,build_rule(*data))
    dlg["dlg"].response(1000)

def clt_ir_cut(dlg,*dontcare):
    try:
       i = int(str(dlg["tr"].get_selection().get_selected_rows()[1][0]))
    except:
       return
    set_clipboard("\n########\n".join(map(str,rule_repr(dlg["t"]["rules"][i]))))
    dlg["dlg"].hide()
    dlg["t"]["rules"].pop(i)
    dlg["dlg"].response(1000)
    
def clt_ir_fns(dlg,*dontcare):
    dlg["dlg"].hide()
    dlg["dlg"].response(1000)
    dlg["t"]["maps"] = edit_maps(dlg["t"]["maps"])

def clt_ir_examine(dlg,*dontcare):
    pass
def clt_ir_list_trivial(dlg,*dontcare):
    pass
def clt_ir_list_justified(dlg,*dontcare):
    pass

def build_token_map(name="",maplist_desc="",x=None):
    if x == None:
        x = {}

    x["name"] = str(name)
    x["maplist"] = []
    for t in maplist_desc.split("\n####\n"):
        if t:
            y = t.split("\n###\n")
            y[0] = y[0]=="True"
            x["maplist"].append(y)


    return x

def map_buildup(maplist):
    fns = []
    for rex,i,o in maplist:
        if rex:
            if not i.endswith("$"):
                i += "$"
            r = re.compile(i)
            fns.append((lambda x,r=r: r.match(x),
                        lambda x,r=r,o=o: r.sub(o,x)))
        else:
            fns.append((lambda x,i=i: x==i,
                        lambda x,i=i,o=o: o if x==i else x))
    return fns

def map_according(builtlist, input):
    for test,replace in builtlist:
        if test(input):
            return replace(input)
    return input

def repr_token_map(m):
    maplist_desc = "\n####\n".join(["\n###\n".join(map(str,x)) for x in m["maplist"]])
    return (m["name"],maplist_desc)

def map_dlg_new(dlg,*dontcare):
    dlg["dlg"].response(1000)
    dlg["dlg"].hide()

    t = edit_map_token(False,"","");
    if not t == (False,"",""):
        dlg["m"]["maplist"].append(t);

def map_dlg_edit(dlg,*dontcare):
    try:
        sel = int(str(dlg["t"].get_selection().get_selected_rows()[1][0]))
    except:
        return

    dlg["dlg"].response(1000)
    dlg["dlg"].hide()

    t = edit_map_token(*dlg["m"]["maplist"][sel]);

    dlg["m"]["maplist"][sel] = t

def map_dlg_copy(dlg,*dontcare):
    try:
        sel = int(str(dlg["t"].get_selection().get_selected_rows()[1][0]))
    except:
        return
    txt = "\n#\n".join(map(str,dlg["m"]["maplist"][sel]))
    set_clipboard(txt)
def map_dlg_paste(dlg,*dontcare):
    txt = get_clipboard().split("\n#\n")
    if not len(txt) == 3:
        return

    dlg["sel"] = len(dlg["m"]["maplist"])

    dlg["m"]["maplist"].append((txt[0]==True,txt[1],txt[2]))
    dlg["dlg"].response(1000)

def map_dlg_cut(dlg,*dontcare):
    try:
        sel = int(str(dlg["t"].get_selection().get_selected_rows()[1][0]))
    except:
        return
    txt = "\n#\n".join(map(str,dlg["m"]["maplist"][sel]))
    set_clipboard(txt)
    dlg["m"]["maplist"].pop(sel)
    dlg["dlg"].response(1000)
def map_dlg_moveup(dlg,*dontcare):
    try:
        sel = int(str(dlg["t"].get_selection().get_selected_rows()[1][0]))
    except:
        return
    if sel <= 0:
        return
    x = dlg["m"]["maplist"][sel-1]
    dlg["m"]["maplist"][sel-1] = dlg["m"]["maplist"][sel]
    dlg["m"]["maplist"][sel] = x    
    dlg["sel"] = sel-1

    dlg["dlg"].response(1000)
def map_dlg_movedown(dlg,*dontcare):
    try:
        sel = int(str(dlg["t"].get_selection().get_selected_rows()[1][0]))
    except:
        return
    if sel >= len(dlg["m"]["maplist"])-1:
        return
    x = dlg["m"]["maplist"][sel+1]
    dlg["m"]["maplist"][sel+1] = dlg["m"]["maplist"][sel]
    dlg["m"]["maplist"][sel] = x    
    dlg["sel"] = sel+1

    dlg["dlg"].response(1000)
def map_dlg_preview(dlg,*dontcare):
    input = dlg["input"].get_text()

    dlg["output"].set_text(map_according(map_buildup(dlg["m"]["maplist"]),input))

def edit_token_map(m=None,parent=None):
    if m == None:
        m = build_token_map()

    cancel = m

    retval = 1000
    prvin = ""
    prvout = ""

    sel = None

    while retval == 1000:

        dlg = map_dlg_build()
        if parent:
            dlg["dlg"].set_transient_for(parent)

        dlg["input"].set_text(prvin)
        dlg["output"].set_text(prvout)

        dlg["name"].set_text(m["name"])

        dlg["m"] = m


        nr = 0
        for r,i,o in m["maplist"]:
            nr += 1
            dlg["l"].append([nr,"RegExp" if r else "Token", i, o])

        if sel!=None:
            dlg["t"].set_cursor(sel);

        retval = dlg["dlg"].run()

        if retval == gtk.ResponseType.CANCEL:
            dlg["dlg"].destroy()
            return cancel

        prvin = dlg["input"].get_text()
        prvout = dlg["output"].get_text()

        m = build_token_map(dlg["name"].get_text())
        m["maplist"] = dlg["m"]["maplist"]

        try:
            sel = int(str(dlg["t"].get_selection().get_selected_rows()[1][0]))
        except:
            sel = None
        if "sel" in dlg:
            sel = dlg["sel"]

        dlg["dlg"].destroy()
    return m

def edit_map_token(regexp, input, output, parent=None):

    cancel = (regexp,input,output)
    retval = 1000

    while retval == 1000:
        dlg = map_line_dlg_build()
        if parent:
            dlg["dlg"].set_transient_for(parent)

        if (regexp):
            dlg["regexp"].set_active(True);

        dlg["input"].set_text(input);
        dlg["output"].set_text(output);

        retval = dlg["dlg"].run()

        if retval == gtk.ResponseType.CANCEL:
            dlg["dlg"].destroy()
            return cancel
        
        input = dlg["input"].get_text()
        output = dlg["output"].get_text()
        regexp = dlg["regexp"].get_active()

        dlg["dlg"].destroy()

    return (regexp,input,output)

def edit_maps(maps,parent=None):
    cancel = copy.deepcopy(maps)
    retval = 1000
    while retval == 1000:
        dlg = map_edit_dialog_build()

        dlg["maps"] = maps

        count = 0
        for m in maps:
            count += 1
            dlg["l"].append([count, m["name"]])

        
        retval = dlg["dlg"].run()

        maps = dlg["maps"]
        
        if retval == gtk.ResponseType.CANCEL:
            dlg["dlg"].destroy()
            return cancel

        dlg["dlg"].destroy()

    return dlg["maps"]


def map_edit_dialog_new(dlg,*dontcare):
    dlg["dlg"].response(1000)
    dlg["maps"].append(edit_token_map())
    
def map_edit_dialog_edit(dlg,*dontcare):
    try:
        sel = int(str(dlg["t"].get_selection().get_selected_rows()[1][0]))
    except:
        return

    dlg["dlg"].response(1000)
    dlg["dlg"].hide()

    dlg["maps"][sel] = edit_token_map(dlg["maps"][sel])

def map_edit_dialog_copy(dlg,*dontcare):
    try:
        sel = int(str(dlg["t"].get_selection().get_selected_rows()[1][0]))
    except:
        return

    txt = "\n######\n".join(map(str,repr_token_map(dlg["maps"][sel])))
    set_clipboard(txt)

def map_edit_dialog_paste(dlg,*dontcare):
    txt = get_clipboard().split("\n######\n")
    if not len(txt) == 2:
        return
    dlg["dlg"].response(1000)
    dlg["dlg"].hide()
    dlg["maps"].append(build_token_map(*txt))


def map_edit_dialog_cut(dlg,*dontcare):
    try:
        sel = int(str(dlg["t"].get_selection().get_selected_rows()[1][0]))
    except:
        return

    txt = "\n######\n".join(map(str,repr_token_map(dlg["maps"][sel])))
    set_clipboard(txt)
    dlg["maps"].pop(sel)

    dlg["dlg"].response(1000)
    dlg["dlg"].hide()

def build_condition(name="",expression="False",slash=False,case=False,x=None):
    if x == None:
        x = {}

    x["name"] = name
    x["expression"] = expression
    x["slash"] = slash
    x["case"] = case

    return x

def condition_repr(x):
    return (x["name"],x["expression"],x["slash"],x["case"])

def eval_condition_expression(expr, premises, names=[]):
    """ This routine take as premise-vector *premises* and a
        conditional expression string *expr*, and
        evaluates whether the condition is met by *expr*;
        *names* may be used to provide a vector of user-names
        for each premise
        --> True, if premises satisfies *expr*,
        --> False, else."""
    g = {"____builtinpremises":premises,"re":re} # global variable dictionary

    for (i,name) in zip(range(len(names)),names):
        if name:
            g[name] = premises[i]

    ex = expr.replace("#", "____builtinpremises")
    try:
        if eval(ex,g):
            return True

    except:       
        return False
    #unsatisfied
    return False

def condition_match(x,premises):
    raise ("NOT IMPLEMENTED YET")
    return eval_condition_expression(x["expression"],vector,names)

def edit_condition(x,rule,task):
    if x == None:
        x = build_condition()
    cancel = copy.deepcopy(x)
    retval = 1000
    while retval == 1000:
        dlg = condition_editor_dlg_build()
        dlg["desc"].set_text(x["name"])
        dlg["cond"].set_text(x["expression"])
        dlg["rule"] = rule
        dlg["task"] = task
        dlg["slash"].set_active(x["slash"])
        dlg["case"].set_active(x["case"])
        retval = dlg["dlg"].run()
        if retval == gtk.ResponseType.CANCEL:
            dlg["dlg"].destroy()
            return cancel
        x["expression"] = dlg["cond"].get_text()
        x["name"] = dlg["desc"].get_text()
        x["slash"] = dlg["slash"].get_active()
        x["case"] = dlg["case"].get_active()
        dlg["dlg"].destroy()
    return x

#TODO: Fix this to work with conditions instead of premises :)
def rdc_new(dlg,*dontcare):
    r = dlg["r"]
    dlg["dlg"].hide()

    dlg["r"]["premises"].append(edit_pattern(build_pattern(),dlg["task"]))
    dlg["dlg"].response(1000)

def rdc_edit(dlg,*dontcare):
    try:
       i = int(str(dlg["tp"].get_selection().get_selected_rows()[1][0]))
    except:
        return
    r = dlg["r"]
    dlg["dlg"].hide()

    dlg["r"]["premises"][i] = edit_pattern(dlg["r"]["premises"][i],dlg["task"])
    dlg["dlg"].response(1000)

def rdc_copy(dlg,*dontcare):
    try:
       i = int(str(dlg["tp"].get_selection().get_selected_rows()[1][0]))
    except:
        return
    r = dlg["r"]
    set_clipboard("\n###\n".join(map(str,pattern_repr(r["premises"][i]))))
def rdc_cut(dlg,*dontcare):
    try:
       i = int(str(dlg["tp"].get_selection().get_selected_rows()[1][0]))
    except:
        return
    r = dlg["r"]
    set_clipboard("\n###\n".join(map(str,pattern_repr(r["premises"][i]))))
    dlg["dlg"].hide()
    dlg["dlg"].response(1000)
    r["premises"].pop(i)
def rdc_paste(dlg,*dontcare):
    c = get_clipboard().split("\n###\n")
    if len(c) != 4:
        return
    p = build_pattern(*c)
    try:
       i = int(str(dlg["tp"].get_selection().get_selected_rows()[1][0]))
    except:
       i = len(dlg["r"]["premises"])

    dlg["dlg"].hide()
    dlg["dlg"].response(1000)
    dlg["r"]["premises"].insert(i,p)
    

#
# MAIN
#



t = build_template("""
            Die /Helligkeit/ an der /Lampe A/ ist genauso groß, wie/ die/ Helligkeit/ an der /Lampe B/.
            Der /Widerstand/ an der /Lampe B/ ist genauso groß, wie/ der/ Widerstand/ an der /Lampe C/.
            """,
            """**/Die /Helligkeit/**
**/Der/Widerstand/**""","1,3,5,6,8,10")

task = build_task()

task["templates"].append(t)

r = build_rule()

r = edit_rule(r,task)
#print(rule_repr(r))
#edit_rule(r,task)


#p = build_pattern()

#edit_pattern(p,task)

#edit_maps(edit_maps(task["maps"]))


#task = edit_task(task)

#print(repr(task))


