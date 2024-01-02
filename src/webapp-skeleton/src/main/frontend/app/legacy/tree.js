/*****************************************
 HBS tree manager
 ******************************************/

//function Tree(customTypes, customMenu) {
export default class Tree {

  cid;
  ajaxUrl;
  types = {
    HBSROOT: {valid_children: ["REGIONE"], icon: "fa fa-circle treeIconSize"},
    REGIONE: {valid_children: ["ULSS"], icon: "fa fa-building treeIconSize"},
    ULSS: {valid_children: ["UOC", "ARPAV"], icon: "fa fa-hospital-o treeIconSize"},
    UOC: {valid_children: ["UOS"], icon: "fa fa-medkit treeIconSize"},
    UOS: {valid_children: ["none"], icon: "fa fa-user-md treeIconSize"},
    ARPAV: {valid_children: ["none"], icon: "fa fa-h-square treeIconSize"}
  };

  constructor(customTypes, customMenu)
  {
    if (customTypes != null) {
      types = customTypes;
    }
  }

  //Load HBS tree tree (form: f_HBS_management)
  initHBSTree = function (hbsTree) {
    this.initHBSTree(hbsTree, 'hbsManager');
  };

  initHBSTree = function (hbsTree, treeType) {

    var operation = 'HbsGetTree';
    if (treeType == 'employeeManager') {
      operation = 'HbsGetTree4CurrentEmployeeRole';
    } else if (treeType == 'codeValueRole') {
      operation = 'HbsGetTree4CodeValueRole';
    }

    this.buildTree(hbsTree, treeType, this.ajaxUrl, operation, null, null);
  }

  /**
   * Builds a html tree. Used at login to select sdloc, managing employee to enable sdlocs, managing sdlocs and managing process security
   * @param hbsTree div that will contain the tree
   * @param treeType can be: login, employeeManager, hbsManager or codeValueRole
   * @param ajaxUrl
   * @param operation
   * @param passedcid
   * @param data
   */

  buildTree = function (hbsTree, treeType, ajaxUrl, operation, passedcid, data) {

    var plugins = ['types', 'massload'];
    var treecid = null;
    if (passedcid != null)
      treecid = passedcid;
    else if (this.cid != null)
      treecid = this.cid;

    var ajaxData = null;

    var self = this;

    if (treeType == 'employeeManager' || treeType == 'login' || treeType == 'codeValueRole') {
      plugins.push("checkbox");
      plugins.push("changed");
    }

    var contextmenu = null;
    if (treeType == 'hbsManager') {
      contextmenu = {items: this.customMenu}
      plugins.push("contextmenu");
    }

    var relativePath = "";
    if (treeType == 'login') {
      relativePath = '../../'
    }

    hbsTree.jstree('destroy');

    hbsTree.jstree({
      'core': {
        'data': { //load data onclick, opening node
          type: 'post',
          url: self.ajaxUrl,
          data: function (node) {
            if (treeType == 'login') {
              return {operation: 'HbsGetTree4CurrentUser', cid: treecid, selectedRole: data};
            } else {
              if (node.id === '#') {
                return {operation: operation, cid: treecid, levelOfDepth: 3};
              } else {
                return {operation: operation, cid: treecid, parentId: node.id, levelOfDepth: 1};
              }
            }
          }
        },
        check_callback: true
      },
      'types': this.types,
      plugins: plugins,
      contextmenu: contextmenu,
      checkbox: {three_state: false}
    });

    // INJECT
    hbsTree.bind("select_node.jstree", function (event, data) {
      var id = data.node.id;
      var injectConfig = 'btnTree;injectConfig'; //used in process MANAGEALL of the HBS (SdL)
      if (id != undefined && treeType == 'hbsManager') {
        injectAndrerenderDetail(id, injectConfig);
      }

    });
    // CREATE
    hbsTree.bind("create_node.jstree", function (event, data) {
      var object_name = data.node.text;
      var parent_id = data.parent;

      jQuery.ajax({
        type: "post",
        url: self.ajaxUrl,
        data: {operation: "HbsCreate", id: parent_id, name: object_name, node_type: data.node.type},
        success: function (response, textStatus, jqXHR) {
          data.node.id = response.id;
          data.node.type = response.type;
        },
        error: function (jqXHR, textStatus, errorThrown) {
          alert(jqXHR.responseText);
        }
      });
    });
    // RENAME
    hbsTree.bind("rename_node.jstree", function (event, data) {
      var id = data.node.id;
      var object_name = data.text;

      jQuery.ajax({
        type: "post",
        url: self.ajaxUrl,
        data: {operation: "HbsRename", id: id, name: object_name},
        success: function (response, textStatus, jqXHR) {
          data.instance.refresh();
        },
        error: function (jqXHR, textStatus, errorThrown) {
          data.instance.refresh();
          alert(jqXHR.responseText);
        }
      });
    });
    // DELETE
    hbsTree.bind("delete_node.jstree", function (event, data) {
      var id = data.node.id;

      jQuery.ajax({
        type: "post",
        url: self.ajaxUrl,
        data: {operation: "HbsDelete", id: id},
        error: function (jqXHR, textStatus, errorThrown) {
          alert(jqXHR.responseText);
        }
      });
    });
    //CHANGE
    hbsTree.on("changed.jstree", function (e, data) {
      if (treeType == 'employeeManager' || treeType == 'login' || treeType == 'codeValueRole') {

        if (data.action != 'select_node' && data.action != 'deselect_node') {
          return;
        }

        if (treeType != 'login' && typeof somethingChanged !== 'undefined') {
          somethingChanged = true;
        }

        if (data.action == 'select_node') {

          var parents = [];
          var selectedAndParentsAndChilds = [data.node.id];
          if (treeType != 'codeValueRole') {
            //find parents
            for (var z = 0; z < data.node.parents.length; z++) {
              var parent = data.node.parents[z];
              if (parent != "#" && data.selected.indexOf(parent) == -1) {
                parents.push(parent);
                selectedAndParentsAndChilds.push(parent);
                data.instance.select_node(parent, true);
              }
            }
            //find childs
            selectedAndParentsAndChilds.push.apply(selectedAndParentsAndChilds, data.node.children_d);
            data.instance.select_node(data.node.children_d, true);
          }
          //operation
          var selectOperation;
          if (treeType == 'login') {
            selectOperation = 'HbsSelect4CurrentUser';
          } else if (treeType == 'employeeManager') {
            selectOperation = 'HbsSelect';
          } else if (treeType == 'codeValueRole') {
            selectOperation = 'HbsSelect4CodeValueRole';
          }
          //select post
          jQuery.ajax({
            type: 'post',
            url: self.ajaxUrl,
            data: {
              operation: selectOperation,
              cid: treecid,
              id: selectedAndParentsAndChilds,
              selected: data.node.id,
              parents: parents,
              'class': 'com.phi.entities.role.ServiceDeliveryLocation'
            },
            error: function (jqXHR, textStatus, errorThrown) {
              alert(jqXHR.responseText);
            },
            success: function (response, textStatus, jqXHR) {
              if (treeType == 'login') {
                if (data.selected.length > 0) {
                  jQuery("#ok").removeAttr('disabled');
                }
              } else if (treeType == 'employeeManager') {
                reRendeOKButton();
              }
            }
          });

        } else if (data.action == 'deselect_node') {
          var selectedAndChilds = [data.node.id];
          if (treeType != 'codeValueRole') {
            //find childs
            selectedAndChilds.push.apply(selectedAndChilds, data.node.children_d);
            data.instance.deselect_node(data.node.children_d, true);
          }
          //operation
          var unselectOperation;
          if (treeType == 'login') {
            unselectOperation = 'HbsUnselect4CurrentUser';
          } else if (treeType == 'employeeManager') {
            unselectOperation = 'HbsUnselect';
          } else if (treeType == 'codeValueRole') {
            unselectOperation = 'HbsUnselect4CodeValueRole';
          }
          //unselect post
          jQuery.ajax({
            type: 'post',
            url: self.ajaxUrl,
            data: {
              operation: unselectOperation,
              cid: treecid,
              id: selectedAndChilds,
              unselected: data.node.id,
              'class': 'com.phi.entities.role.ServiceDeliveryLocation'
            },
            error: function (jqXHR, textStatus, errorThrown) {
              alert(jqXHR.responseText);
            },
            success: function (response, textStatus, jqXHR) {
              if (treeType == 'login') {
                if (data.selected.length == 0) {
                  jQuery("#ok").attr('disabled', 'disabled');
                }
              } else if (treeType == 'employeeManager') {
                reRendeOKButton();
              }
            }
          });
        }
      }
    });
  }

  customMenu = function (node) {
    var menu = this.menu(node);

    var createREGION = {
      "label": "Aggiungi Regione",
      "action": function (obj) {
        createNode('REGIONE', obj.reference);
      }
    };
    var createULSS = {
      "label": "Aggiungi ULSS",
      "action": function (obj) {
        createNode('ULSS', obj.reference);
      }
    };
    var createUOC = {
      "label": "Aggiungi UOC",
      "action": function (obj) {
        createNode('UOC', obj.reference);
      }
    };
    var createUOS = {
      "label": "Aggiungi UOS",
      "action": function (obj) {
        createNode('UOS', obj.reference);
      }
    };
    var createARPAV = {
      "label": "Aggiungi ARPAV",
      "action": function (obj) {
        createNode('ARPAV', obj.reference);
      }
    };

    if (node.type == 'HBSROOT') {
      menu.createREGION = createREGION;
    } else if (node.type == 'REGIONE') {
      menu.createULSS = createULSS;
    } else if (node.type == 'ULSS') {
      menu.createUOC = createUOC;
      menu.createARPAV = createARPAV;
    } else if (node.type == 'UOC') {
      menu.createUOS = createUOS;
    }

    return menu;
  }.bind(this);

  menu = function (node) {
    var menu = {};

    if (hasChildren(node)) {
      menu.deleteItem = {
        "label": "Cancella",
        "action": function (data) {
          var inst = jQuery.jstree.reference(data.reference);
          var obj = inst.get_node(data.reference);
          inst.delete_node(obj);
        }
      };
    }
    menu.rename = {
      "label": "Rinomina",
      "action": function (data) {
        var inst = jQuery.jstree.reference(data.reference);
        var obj = inst.get_node(data.reference);
        inst.edit(obj);
      }
    };
    if (areAllChildrenDisabled(node) && !isDisabled(node)) {
      menu.disable = {
        "label": "Disabilita",
        "action": function (data) {
          var inst = jQuery.jstree.reference(data.reference);
          var obj = inst.get_node(data.reference);

          jQuery.ajax({
            type: "post",
            url: this.ajaxUrl,
            data: {operation: "HbsDisable", id: obj.id},
            success: function (response, textStatus, jqXHR) {
              obj.state.disabled = true;
              inst.refresh();
            },
            error: function (jqXHR, textStatus, errorThrown) {
              alert(jqXHR.responseText);
            }
          });
        }
      };
    }
    if (nodeCanBeEnabled(node)) {
      menu.reEnable = {
        "label": "Abilita",
        "action": function (data) {
          var inst = jQuery.jstree.reference(data.reference);
          var obj = inst.get_node(data.reference);

          jQuery.ajax({
            type: "post",
            url: this.ajaxUrl,
            data: {operation: "HbsReEnable", id: obj.id},
            success: function (response, textStatus, jqXHR) {
              obj.state.disabled = false;
              inst.refresh();
            },
            error: function (jqXHR, textStatus, errorThrown) {
              alert(jqXHR.responseText);
            }
          });
        }
      };
    }
    return menu;
  }

  createNode = function (type, reference) {
    var inst = jQuery.jstree.reference(reference);
    var obj = inst.get_node(reference);
    inst.create_node(obj, {type: type}, "last", function (new_node) {
      setTimeout(function () {
        inst.edit(new_node);
      }, 0);
    });
  }

  isDisabled = function (node) {
    return node.state.disabled;
  }


  nodeCanBeEnabled = function (node) {
//		var liElement = jQuery(node.attr('class'));
//		var liParent = jQuery(node.parent().parent().attr('class'));
//
//		if(((liElement.selector).indexOf('jstree-disabled') >= 0) && ((liParent.selector).indexOf('jstree-disabled')) == -1){
//			return true;
//		}
    return node.state.disabled; //FIXME check parent is enabled!
  }

  hasChildren = function (node) {
    //var liChilds = jQuery(node.find('li'));
    if (jQuery(node).hasClass('jstree-leaf')) {
      return true;
    } else {
      return false;
    }
  }


  areAllChildrenDisabled = function (node) {
    //var liElement = jQuery(node.attr('class'));
//		var liChilds = jQuery(node.find('li'));
    //.attr('class')
    //if(jQuery(node).hasClass('jstree-leaf')){
    if (node.children.length == 0) {
      //if((liElement.selector).indexOf('jstree-closed') == 0){
      if (node.state.opened == true) {
        return false;
      } else {
        return true
      }
      //} else if( ( (liElement.selector).indexOf('jstree-closed') == 0) && (liChilds.length == 0) ){
    } else if (node.state.opened == true && node.children == 0) {
      return false;
    } else {
      //FIXME check all children disabled!!!
      //for(z=0; z < liChilds.length ; z++){
//			for(z=0; z < node.children.length ; z++){
//				//if (jQuery(liChilds[z]).attr('class').indexOf('jstree-disabled') == -1){
//				if (node.children[z].state.disabled == -1){
//					return false;
//				}
//
//			}

      return true;
    }

    // return false;
  }


  //-----  end HBS tree manager  -------------------------------------------------------


  //FIXME: la funzione accetta 11 parametri ma c'è la documentazione solo per 5

  //Dictionary tree widget:
  //create a tree containing dictionary values of codeSystem, under domain.
  //treeElement: div that will contain tree
  //displayNameAndTranslation: if true renders: displayName - translation; false: translation
  //dataComponent: What will be written in the tree node as label. Allowed value:  "displayName - translation", "translation", "[code] displayName"
  //a4jF4inject: name of a4j function called onclick of a leaf
  //injectIntoConversationName: onClick injects object into injectIntoConversationName
  initTreeDictionary = function (treeElement, codeSystem, domain, codeValueClass, dataComponent, a4jF4inject, injectIntoConversationName, injectOnlyLeaf, treeType, selected, currentNode) {

    this.buildTreeDictionary(treeElement, codeSystem, domain, codeValueClass, dataComponent, a4jF4inject, injectIntoConversationName, null, injectOnlyLeaf, treeType, selected, currentNode);
  }


  buildTreeDictionary = function (treeElement, codeSystem, domain, codeValueClass, dataComponent, a4jF4inject, injectIntoConversationName, data, injectOnlyLeaf, treeType, selected, currentNode) {

    if (codeSystem == null || codeSystem == "" || codeValueClass == null || codeValueClass == "") {
      return;
    }

    var plugins = ["types", "search", 'massload'];
    var contextmenu = null;
    if (treeType == 'editableCvTree') {
      contextmenu = {items: this.dictionaryMenu}
      plugins.push("contextmenu");
    }

    var self = this;
    if (selected == undefined || selected == "") {
      selected = [];
    } else {
      selected = selected.split(", ");
    }

    treeElement.jstree({
      plugins: plugins,
      contextmenu: contextmenu,
      'core': {
        'data': { //load data onclick, opening node
          type: 'post',
          url: self.ajaxUrl,
          data: function (node) {
            if (node.id == '#') {
              console.log('get tree' + self.cid);
              return {
                operation: "DictionaryGetTree",
                cid: self.cid,
                codeSystem: codeSystem,
                domain: domain,
                dataComponent: dataComponent,
                levelOfDepth: 1,
                codeValueClass: codeValueClass
              };
            } else {
              return {
                operation: "DictionaryGetTree",
                cid: self.cid,
                parentId: node.id,
                dataComponent: dataComponent,
                levelOfDepth: 1,
                codeValueClass: codeValueClass
              };
            }
          }
        },
        check_callback: true
      },
      search: {
        show_only_matches: true,
        case_insensitive: true,
        ajax: { //search, server loads parents of search results
          type: 'post',
          url: this.ajaxUrl,
          data: {
            operation: "DictionarySearch",
            cid: self.cid,
            codeValueClass: codeValueClass
          },
          beforeSend: function (jqXHR, settings) {
            blockUI();
          },
          error: function (jqXHR, textStatus, errorThrown) {
            unblockUI();
          }
        }
      },
      'types': {
        ROOT: {icon: "fa fa-folder-o treeIconSize"},
        TOPLEVEL: {icon: "fa fa-folder-o treeIconSize"},
        DOMAIN: {icon: "fa fa-folder-o treeIconSize"},
        CODE: {icon: "fa fa-leaf treeIconSize"},
        PARAMETER: {icon: "fa fa-cog treeIconSize"}
      }
    });

    treeElement.bind('search.jstree', function (e, data) {
      unblockUI();
    });

    treeElement.bind('loaded.jstree', function (e, data) {
      data.instance.set_state({core: {open: selected, 'selected': [currentNode]}});
    });

    // INJECT
    treeElement.bind("select_node.jstree", function (event, data) {
      var id = data.node.id;
      var a4jFtoBeCalled = new Function('id', 'codeValueClass', 'injectInto', a4jF4inject + '(id,codeValueClass,injectInto)');
      a4jFtoBeCalled.call(null, id, codeValueClass, injectIntoConversationName);
    });
    if (injectOnlyLeaf) {
      //INJECT ONLY LEAF
      treeElement.bind("before.jstree", function (e, data) {
        if (data.func === "select_node" && !data.inst.is_leaf(data.args[0])) {
          e.stopImmediatePropagation();
          return false;
        }
      });
    }


    treeElement.bind("delete_node.jstree", function (event, data) {
      var id = data.node.id;

      jQuery.ajax({
        type: "post",
        url: self.ajaxUrl,
        data: {operation: "CvDelete", id: id, cid: self.cid},
        error: function (jqXHR, textStatus, errorThrown) {
          //jQuery.jstree.rollback(data.rlbk);
          alert(jqXHR.responseText);
        }
      });
    });


  }


  dictionaryMenu = function (node) {

    var node_type = node.original.type;

    var menu = {};

    var self = this;

    function createCV(new_node) {
      var object_name = new_node.text;
      var parent_id = new_node.parent;

      jQuery.ajax({
        type: "post",
        url: self.ajaxUrl,
        data: {
          operation: "CvCreate",
          id: parent_id,
          name: object_name,
          node_type: new_node.original.type,
          generateId: false,
          cid: self.cid
        },
        success: function (response, textStatus, jqXHR) {
          new_node.id = response.id;
          new_node.type = response.type;
        },
        error: function (jqXHR, textStatus, errorThrown) {
          //jQuery.jstree.rollback(data.rlbk);
          alert(jqXHR.responseText);
        }
      });
    };

    var createCode = {
      "label": "Aggiungi codice",
      "action": function (data) {
        var inst = jQuery.jstree.reference(data.reference);
        var obj = inst.get_node(data.reference);
        inst.create_node(obj, {type: 'CODE'}, "last", function (new_node) {
          inst.edit(new_node, 'nuovo', createCV);
        });
      }
    };
    var createDomain = {
      "label": "Aggiungi dominio",
      "action": function (data) {
        var inst = jQuery.jstree.reference(data.reference);
        var obj = inst.get_node(data.reference);
        inst.create_node(obj, {type: 'DOMAIN'}, "last", function (new_node) {
          setTimeout(function () {
            inst.edit(new_node);
          }, 0);
        });
      }
    };
    var createTopLevelDomain = {
      "label": "Aggiungi dominio top level",
      "action": function (data) {
        var inst = jQuery.jstree.reference(data.reference);
        var obj = inst.get_node(data.reference);
        inst.create_node(obj, {type: 'TOPLEVEL'}, "last", function (new_node) {
          setTimeout(function () {
            inst.edit(new_node);
          }, 0);
        });
      }
    };

    var deleteItem = {
      "label": "Elimina",
      "action": function (data) {
        var inst = jQuery.jstree.reference(data.reference);
        var obj = inst.get_node(data.reference);
        inst.delete_node(obj);
      }
    };

    //control menu items
    if (node_type == 'ROOT') {
      menu.createCode = createCode;
      menu.createTopLevelDomain = createTopLevelDomain;
    }
    else if (node_type == 'TOPLEVEL') {
      menu.createCode = createCode;
      menu.createDomain = createDomain;
      menu.deleteItem = deleteItem;
    }
    else if (node_type == 'DOMAIN') {
      menu.createCode = createCode;
      menu.createDomain = createDomain;
      menu.deleteItem = deleteItem;
    }
    else if (node_type == 'CODE') {
      menu.deleteItem = deleteItem;
    }
    else if (node_type == 'PARAMETER') {
      menu.createCode = createCode;
      menu.deleteItem = deleteItem;
    }

    return menu;
  }.bind(this);


  buildCheckTreeDictionary = function (chkTree, codeSystem, domain, codeValueClass, dataComponent, toReloadFunction, injectIntoConversationName, data, checkOnlyLeaves, treeType, selected, currentNodes, listName) {

    var plugins = ['types', 'massload', 'checkbox', 'changed'];
    var processReadOnly = jQuery('#processReadOnly').val();

    var self = this;
    if (selected == undefined || selected == "") {
      selected = [];
    } else {
      selected = selected.substring(1, selected.length - 1);
      selected = selected.split(", ");
    }
    if (currentNodes == undefined || currentNodes == "") {
      currentNodes = [];
    } else {
      currentNodes = currentNodes.substring(1, currentNodes.length - 1);
      currentNodes = currentNodes.split(", ");
    }

    if (checkOnlyLeaves) {
      chkTree.addClass('checkOnlyLeaves');
    } else {
      chkTree.removeClass('checkOnlyLeaves');
    }

    chkTree.jstree({
      plugins: plugins,
      checkbox: {three_state: false},
      'core': {
        'data': { //load data onclick, opening node
          type: 'post',
          url: self.ajaxUrl,
          data: function (node) {
            if (node.id == '#') {
              console.log('get tree' + self.cid);
              return {
                operation: "DictionaryGetTree",
                cid: self.cid,
                codeSystem: codeSystem,
                domain: domain,
                dataComponent: dataComponent,
                levelOfDepth: 1,
                codeValueClass: codeValueClass,
                disabled: processReadOnly
              };
            } else {
              return {
                operation: "DictionaryGetTree",
                cid: self.cid,
                parentId: node.id,
                dataComponent: dataComponent,
                levelOfDepth: 1,
                codeValueClass: codeValueClass,
                disabled: processReadOnly
              };
            }
          }
        },
        check_callback: true
      },
      'types': {
        ROOT: {icon: "fa fa-folder-o treeIconSize"},
        TOPLEVEL: {icon: "fa fa-folder-o treeIconSize"},
        DOMAIN: {icon: "fa fa-folder-o treeIconSize"},
        CODE: {icon: "fa fa-leaf treeIconSize"}
      }
    });

    chkTree.bind('loaded.jstree', function (e, data) {
      data.instance.set_state({core: {open: selected, 'selected': currentNodes}});
    });

    //CHANGE
    chkTree.on("changed.jstree", function (e, data) {

      if (data.action != 'select_node' && data.action != 'deselect_node') {
        return;
      }

      if (typeof somethingChanged !== 'undefined') {
        somethingChanged = true;
      }

      if (data.action == 'select_node') {

        var parents = [];
        var selectedAndParentsAndChilds = [data.node.id];
        //find parents
        for (var z = 0; z < data.node.parents.length; z++) {
          var parent = data.node.parents[z];
          if (parent != "#" && data.selected.indexOf(parent) == -1) {
            parents.push(parent);
            selectedAndParentsAndChilds.push(parent);
            data.instance.select_node(parent, true);
          }
        }

        //operation
        var selectOperation = 'CvSelect';

        //select post
        jQuery.ajax({
          type: 'post',
          url: self.ajaxUrl,
          data: {
            operation: selectOperation,
            cid: self.cid,
            id: selectedAndParentsAndChilds,
            selected: data.node.id,
            parents: parents,
            codeValueClass: codeValueClass,
            listName: listName
          }
        });

        if (toReloadFunction) {
          var a4jFtoBeCalled = new Function(toReloadFunction + '()');
          a4jFtoBeCalled.call();
        }

      } else if (data.action == 'deselect_node') {
        var selectedAndChilds = [data.node.id];
        //find childs
        selectedAndChilds.push.apply(selectedAndChilds, data.node.children_d);
        data.instance.deselect_node(data.node.children_d, true);

        //operation
        var unselectOperation = 'CvUnselect';

        //unselect post
        jQuery.ajax({
          type: 'post',
          url: self.ajaxUrl,
          data: {
            operation: unselectOperation,
            cid: self.cid,
            id: selectedAndChilds,
            unselected: data.node.id,
            codeValueClass: codeValueClass,
            listName: listName
          }
        });

        if (toReloadFunction) {
          var a4jFtoBeCalled = new Function(toReloadFunction + '()');
          a4jFtoBeCalled.call();
        }
      }
    });
  }

  initTreeCV = function (dicTreeCV, selectedIDCV, confirmed, conversationId, type, conversationName) {

    dicTreeCV.bind("before.jstree", function (e, data) {

      if (data.func === "select_node" && !data.inst.is_leaf(data.args[0])) {

        data.inst.toggle_node(data.args[0]).closest;
        e.stopImmediatePropagation();
        return false;
      }

    });

    dicTreeCV.jstree({
      plugins: ["themes", "html_data", "core", "ui", "crrm", "types", "search"],
      themes: {
        theme: "default",
        dots: true,
        icons: false
      },
      /*core:{
       initially_open : [selectedIDCV]

       },*/


      search: {
        case_insensitive: true,
        show_only_matches: true
      }/*,
       ui :{
       select_limit : 1,
       initially_select : [selectedIDCV]

       }*/

    });
    // INJECT
    dicTreeCV.bind("select_node.jstree", function (event, data) {
      var id = data.node.id;
      var conversationName = data.rslt.obj.attr('conversationName');
      injectAndrerenderDetail(id, conversationName);
    });

    /*if(!selectedIDCV){
     dicTreeCV.bind('loaded.jstree', function(e, data){
     //Open nodes on load (until 1'th level)
     var depth = 1;
     data.inst.get_container().find('li').each(function(i) {
     if(data.inst.get_path(jQuery(this)).length<=depth){
     data.inst.open_node(jQuery(this));
     }
     });
     });
     }*/

  }
}
