import * as jQuery from 'jquery/dist/jquery';
import 'jstree';
import Phi from "./js";
import { Inject, Injectable, NgZone } from '@angular/core';
import { ViewManager } from '../services/view-manager';
import { ServiceDeliveryLocationActionService } from '../services/actions';
import { TreeComponent } from '../widgets/tree/tree.component';
import { ProcessManager } from '../services/process-manager';
import { DictionaryManager } from '../services/dictionary-manager';

/*****************************************
 HBS tree manager
 ******************************************/

@Injectable()
export class PhiTree {
  hbsNodeType: string;
  restUrl: string;
  treeRef: any;
  cid: string;
  codeSystem;

  constructor(@Inject('apiUrl') protected apiUrl,
              private serviceDeliveryLocationAction: ServiceDeliveryLocationActionService,
              private viewManager: ViewManager,
              private processManager: ProcessManager,
              private dictionaryManager: DictionaryManager,
              private zone: NgZone) {
    this.restUrl = apiUrl+'resource/rest/tree/';
  }

  get phi() {
    return window['Phi'];
  }

  initTreeProcess(container, injectCallback) {
    this.zone.run(() => {

      this.processManager.processsecurity().then((procSecurityList: Array<any>) => {

        const tree: TreeComponent = this.viewManager.appendComponentTo(container, TreeComponent);
        tree.listElementsExpression = procSecurityList;
        tree.styleClass = 'max-height-100x100';
        // tree.expand = 2;
        tree.change.subscribe(event => {
          if (injectCallback) {
            injectCallback(event.value.id);
          }
        });

      });
    });
  }

  initHBSTree(hbsTree, treeType) {
    this.cid = jQuery('#conversationId').val();
/*
    let types = {
      HBSROOT: { valid_children: ["ASL"], icon: "fa fa-h-square treeIconSize"},
      ORG: { valid_children: ["ORG"], icon: "fa fa-building-o  treeIconSize"},
      ASL: { valid_children: ["ASL"], icon: "fa fa-hospital-o  treeIconSize"},
      HOSP: { valid_children: ["ORG"], icon: "fa fa-hospital-o  treeIconSize"},
      DIS: { valid_children: ["ORG"],icon: "fa fa-user-md treeIconSize"},
      LOCATION: { valid_children: ["none"], icon: "fa fa-building  treeIconSize"},
      UOF: { valid_children: ["UPR"], icon: "fa fa-hospital-o  treeIconSize"},
      UPR: { valid_children: ["none"], icon: "fa fa-building-o  treeIconSize"},
      UP: { valid_children: ["UD"], icon: "fa fa-stethoscope  treeIconSize"},
      UD: { valid_children: ["none"], icon: "fa fa-address-book-o  treeIconSize"},
      WARD: { valid_children: ["none"], icon: "fa fa-medkit treeIconSize"},
      Room: { valid_children: ["none"], icon: "fa fa-ambulance  treeIconSize"},
      Bed: { valid_children: ["none"], icon: "fa fa-bed  treeIconSize"}
    };
*/
    let types = {
      HBSROOT: { valid_children: ["OUP","DIS","LOC","ORG"], icon: "fa fa-h-square treeIconSize"},
      DIS: { valid_children: ["none"],icon: "fa fa-user-md treeIconSize"},
      LOC: { valid_children: ["none"], icon: "fa fa-building treeIconSize"},
      ORG: { valid_children: ["HOSP","DIS"], icon: "fa fa-building-o treeIconSize"},
      HOSP: { valid_children: ["UOF"], icon: "fa fa-hospital-o treeIconSize"},
      OUP: { valid_children: ["UOF"], icon: "fa fa-hospital-o treeIconSize"},
      UOF: { valid_children: ["WARD","UP","UPR"], icon: "fa fa-hospital-o treeIconSize"},
      UPR: { valid_children: ["WARD","UP"], icon: "fa fa-building-o treeIconSize"},
      UP: { valid_children: ["UD"], icon: "fa fa-stethoscope treeIconSize"},
      UD: { valid_children: ["none"], icon: "fa fa-address-book-o treeIconSize"},
      WARD: { valid_children: ["ROOM","UD"], icon: "fa fa-medkit treeIconSize"},
      ROOM: { valid_children: ["BED"], icon: "fa fa-inbox treeIconSize"},
      BED: { valid_children: ["none"], icon: "fa fa-bed treeIconSize"}
    };

    this.buildHBSTree(hbsTree, treeType, types, null);
    this.treeRef = hbsTree.jstree();
  }

  buildHBSTreeAngular(container, treeType, types, data) {


      this.serviceDeliveryLocationAction.loadTree(treeType).then(hbsTree => {

        this.zone.run(() => {
          const tree: TreeComponent = this.viewManager.appendComponentTo(container[0], TreeComponent);
          tree.listElementsExpression = [hbsTree];
          tree.expand = 2;
          if (treeType == 'employeeManager'/* || treeType == 'login' || treeType == 'codeValueRole'*/ ) {
            tree.checkbox = true;
          }
          tree.change.subscribe(event => {
            let a4jFtoBeCalled = new Function('id', 'injectAndrerenderDetail(id)');
            a4jFtoBeCalled.call(null, event.value.id);
          });

        });
    });
  }

  /**
   * Builds a html tree. Used at login to select sdloc, managing employee to enable sdlocs, managing sdlocs and managing process security
   * @param hbsTree div that will contain the tree
   * @param treeType can be: login, employeeManager, hbsManager or codeValueRole
   * @param data: contains the selectedRole (for login)
   */
  buildHBSTree(hbsTree, treeType, types, data) {

    let operation = this.restUrl+'hbs/getTree';
    if (treeType == 'employeeManager') {
      operation = this.restUrl+'hbs/getTree4CurrentEmployeeRole';
    } else if (treeType == 'codeValueRole') {
      operation = this.restUrl+'hbs/getTree4CodeValueRole';
    }

    let plugins = ['types', 'massload'];
    let self = this;

    if (treeType == 'employeeManager' || treeType == 'login' || treeType == 'codeValueRole') {
      plugins.push("checkbox");
      plugins.push("changed");
    }

    let contextmenu = null;
    if (treeType == 'hbsManager') {
      contextmenu = {items: this.customMenu};
      plugins.push("contextmenu");
    }

    hbsTree.jstree('destroy');

    hbsTree.jstree({
      'core': {
        'data': { //load data onclick, opening node
          type: 'post',
          url: operation + '?cid=' + this.cid,
          data: function (node) {
            if (treeType == 'login') {
              return {selectedRole: data};
            } else {
              if (node.id === '#') {
                return {levelOfDepth: 3};
              } else {
                return {parentId: node.id, levelOfDepth: 1};
              }
            }
          }
        },
        check_callback: true
      },
      'types': types,
      plugins: plugins,
      contextmenu: contextmenu,
      checkbox: {three_state: false}
    });

    // INJECT
    hbsTree.bind("select_node.jstree",  (event, data) => {
      let id = data.node.id;
      let injectConfig = 'btnTree;injectConfig'; // used in KLINIK process MANAGEALL of the HBS (SdL)
      if (id && treeType === 'hbsManager') {
        window['injectAndrerenderDetail'](id, injectConfig);
      }

    });
    // CREATE
    hbsTree.bind("create_node.jstree", (event, data) => {
      let object_name = data.node.text;
      let parent_id = data.parent;

      jQuery.ajax({
        type: "post",
        url: self.restUrl + "hbs/create" + '?cid=' + this.cid,
        data: {id: parent_id, name: object_name, node_type: data.node.type},
        beforeSend: (jqXHR, settings) => {
          this.phi.blockUI();
        },
        success: (response, textStatus, jqXHR) => {
          data.node.id = response.id;
          data.node.type = response.type;
          this.phi.unblockUI();
        },
        error: (jqXHR, textStatus, errorThrown) => {
          alert(jqXHR.responseText);
          this.phi.unblockUI();
        }
      });
    });
    // RENAME
    hbsTree.bind("rename_node.jstree", (event, data) => {
      let id = data.node.id;
      let object_name = data.text;

      jQuery.ajax({
        type: "post",
        url: self.restUrl + "hbs/rename" + '?cid=' + this.cid,
        data: {id: id, name: object_name},
        beforeSend: (jqXHR, settings) => {
          this.phi.blockUI();
        },
        success: (response, textStatus, jqXHR) => {
          data.instance.refresh();
          this.phi.unblockUI();
        },
        error: (jqXHR, textStatus, errorThrown) => {
          data.instance.refresh();
          alert(jqXHR.responseText);
          this.phi.unblockUI();
        }
      });
    });
    // DELETE
    hbsTree.bind("delete_node.jstree", (event, data) => {
      let id = data.node.id;

      jQuery.ajax({
        type: "post",
        url: self.restUrl + "hbs/delete" + '?cid=' + this.cid,
        data: {id: id},
        beforeSend: (jqXHR, settings) => {
          this.phi.blockUI();
        },
        success: (response, textStatus, jqXHR) => {
          this.phi.unblockUI();
        },
        error: (jqXHR, textStatus, errorThrown) => {
          alert(jqXHR.responseText);
          this.phi.unblockUI();
        }
      });
    });
    //CHANGE
    hbsTree.on("changed.jstree", (e, data) => {
      if (treeType == 'employeeManager' || treeType == 'login' || treeType == 'codeValueRole') {

        if (data.action != 'select_node' && data.action != 'deselect_node') {
          return;
        }

        if (treeType != 'login' && typeof this.phi.somethingChanged !== 'undefined') {
          this.phi.somethingChanged = true;
        }

        if (data.action == 'select_node') {

          let parents = [];
          let selectedAndParentsAndChilds = [data.node.id];
          if (treeType != 'codeValueRole') {
            //find parents
            for (let z = 0; z < data.node.parents.length; z++) {
              let parent = data.node.parents[z];
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
          let selectOperation;
          if (treeType == 'login') {
            selectOperation = 'hbs/select4CurrentUser';
          } else if (treeType == 'employeeManager') {
            selectOperation = 'hbs/select';
          } else if (treeType == 'codeValueRole') {
            selectOperation = 'hbs/select4CodeValueRole';
          }
          //select post
          jQuery.ajax({
            type: 'post',
            url: self.restUrl + selectOperation + '?cid=' + this.cid,
            data: {
              //operation: selectOperation,
              id: selectedAndParentsAndChilds,
              selected: data.node.id,
              parents: parents,
              'class': 'com.phi.entities.role.ServiceDeliveryLocation'
            },
            beforeSend: (jqXHR, settings) => {
              this.phi.blockUI();
            },
            error: (jqXHR, textStatus, errorThrown) => {
              alert(jqXHR.responseText);
              this.phi.unblockUI();
            },
            success: (response, textStatus, jqXHR) => {
              if (treeType == 'login') {
                if (data.selected.length > 0) {
                  jQuery("#ok").removeAttr('disabled');
                }
              } else if (treeType == 'employeeManager') {
                let a4jFtoBeCalled = new Function('reRendeOKButton()');
                a4jFtoBeCalled();
              }
              this.phi.unblockUI();
            }
          });

        } else if (data.action == 'deselect_node') {
          let selectedAndChilds = [data.node.id];
          if (treeType != 'codeValueRole') {
            //find childs
            selectedAndChilds.push.apply(selectedAndChilds, data.node.children_d);
            data.instance.deselect_node(data.node.children_d, true);
          }
          //operation
          let unselectOperation;
          if (treeType == 'login') {
            unselectOperation = 'hbs/unselect4CurrentUser';
          } else if (treeType == 'employeeManager') {
            unselectOperation = 'hbs/unselect';
          } else if (treeType == 'codeValueRole') {
            unselectOperation = 'hbs/unselect4CodeValueRole';
          }
          //unselect post
          jQuery.ajax({
            type: 'post',
            url: self.restUrl + unselectOperation + '?cid=' + this.cid,
            data: {
              //operation: unselectOperation,
              id: selectedAndChilds,
              unselected: data.node.id,
              'class': 'com.phi.entities.role.ServiceDeliveryLocation'
            },
            beforeSend: (jqXHR, settings) => {
              this.phi.blockUI();
            },
            error: (jqXHR, textStatus, errorThrown) => {
              alert(jqXHR.responseText);
              this.phi.unblockUI();
            },
            success: (response, textStatus, jqXHR) => {
              if (treeType == 'login') {
                if (data.selected.length == 0) {
                  jQuery("#ok").attr('disabled', 'disabled');
                }
              } else if (treeType == 'employeeManager') {
                let a4jFtoBeCalled = new Function('reRendeOKButton()');
                a4jFtoBeCalled();
              }
              this.phi.unblockUI();
            }
          });
        }
      }
    });
  }

  customMenu = function (node): any {
    let menu: any = this.getDefaultMenu(node);

    let createORG = {"label": "Aggiungi Azienda", "action": (obj) => this.createNode('ORG', obj.reference)};
    let createHOSP = {"label": "Aggiungi Ospedale","action": (obj) => this.createNode('HOSP', obj.reference)};
    let createUOF = {"label": "Aggiungi Unita Operativa","action": (obj) => this.createNode('UOF', obj.reference)};
    let createUP = {"label": "Aggiungi Ambulatorio","action": (obj) => this.createNode('UP', obj.reference)};
    let createWARD = {"label": "Aggiungi Reparto","action": (obj) => this.createNode('WARD', obj.reference)};
    let createROOM = {"label": "Aggiungi stanza","action": (obj) => this.createNode('ROOM', obj.reference)};
    let createBED = {"label": "Aggiungi letto","action": (obj) => this.createNode('BED', obj.reference)};
    let createUD = {"label": "Aggiungi agenda","action": (obj) => this.createNode('UD', obj.reference)};
    let createOUP = {"label": "Aggiungi Ospedale Unico Plurisede","action": (obj) => this.createNode('OUP', obj.reference)};
    let createLOC = {"label": "Aggiungi Presidio","action": (obj) => this.createNode('LOC', obj.reference)};
    let createUPR = {"label": "Aggiungi Unita Produttiva di Raggruppamento","action": (obj) => this.createNode('UPR', obj.reference)};
    let createDIS = {"label": "Aggiungi Distretto","action": (obj) => this.createNode('DIS', obj.reference)};

    let disableItem = {
      "label": "Disabilita",
      "action": (obj) => {
        jQuery.ajax({
          type: "post",
          url: this.restUrl + "hbs/disable" + '?cid=' + this.cid,
          data: { id:obj.reference.attr('id').substring(0,obj.reference.attr('id').indexOf("_")) },
          beforeSend: (jqXHR, settings) => {
            this.phi.blockUI();
          },
          success: (response, textStatus, jqXHR) => {
            jQuery(obj.reference).addClass('jstree-disabled');
            this.phi.unblockUI();
          },
          error: (jqXHR, textStatus, errorThrown) => {
            alert(jqXHR.responseText);
            this.phi.unblockUI();
          }
        });
      }
    };
    let reEnableItem = {
      "label": "Abilita",
      "action": (obj) => {
        jQuery.ajax({
          type: "post",
          url: this.restUrl + "hbs/reEnable" + '?cid=' + this.cid,
          data: { id:obj.reference.attr('id').substring(0,obj.reference.attr('id').indexOf("_")) },
          beforeSend: (jqXHR, settings) => {
            this.phi.blockUI();
          },
          success: (response, textStatus, jqXHR) => {
            jQuery(obj.reference).removeClass('jstree-disabled');
            this.phi.unblockUI();
          },
          error: (jqXHR, textStatus, errorThrown) => {
            alert(jqXHR.responseText);
            this.phi.unblockUI();
          }
        });
      }
    };

    /*

     VCO
     ****

     * HBSROOT (Strutture ospedaliere)
     * UOP (Ospedale Unico Plurisede)
     * UOF (poliambulatori)
     * WARD (reparto)
     * UP (ambulatorio)
     * UD (agenda)
     * UPR (servizio - poliambulatorio)
     * UP (ambulatorio)
     * UD (agenda)
     * DIS (Distretto)
     * LOC (Presidio)

     BZ
     ***

     * HBSROOT (Strutture ospedaliere)
     * ORG (Azienda)
     * HOSP (Ospedale)
     * UOF (poliambulatori)
     * WARD (reparto)
     * UP (ambulatorio)
     * UD (agenda)
     * UPR (servizio - poliambulatorio)
     * WARD (reparto)
     * DIS (Distretto)

     MERGE:

     HBSROOT ->  UOP, DIS, LOC, ORG
     ORG -> HOSP, DIS
     HOSP -> UOF
     createUOP -> UOF
     UOF -> WARD, UP, UPR
     UPR -> WARD, UP
     UP -> UD

    * */

    //control menu items
    if (node.type == 'HBSROOT') {
      menu.createOUP = createOUP;
      menu.createDIS = createDIS;
      menu.createLOC = createLOC;
      menu.createORG = createORG;

    } else {
      if (this.areAllChildrenDisabled(node) && !this.isDisabled(node)) {
        menu.disable = disableItem;
      }
      if (this.nodeCanBeEnabled(node)) {
        menu.reEnable = reEnableItem;
      }

      if(node.type == 'ORG') {
        menu.createHOSP = createHOSP;
        menu.createDIS = createDIS;
      } else 	if (node.type == 'HOSP') {
        menu.createUOF = createUOF;
      } else if (node.type == 'OUP') {
        menu.createUOF = createUOF;
      } else if (node.type == 'UOF') {
        menu.createUPR  = createUPR;
        menu.createUP   = createUP;
        menu.createWARD = createWARD;
      } else if (node.type == 'UPR') {
        menu.createWARD = createWARD;
        menu.createUP   = createUP;
      } else if (node.type == 'UP') {
        menu.createUD = createUD;
      } else if (node.type == 'WARD') {
        menu.createROOM = createROOM;
        menu.createUD = createUD;
      } else if (node.type == 'ROOM') {
        menu.createBED = createBED;
      }
    }
    return menu;
  }.bind(this);

  getDefaultMenu = function(node): any {
    let menu: any = {};
    if(!this.hasChildren(node) && (this.isOpened(node) || this.isLeaf(node))){
      menu.deleteNode = {
        "label": "Cancella",
        "action": (data) => this.treeRef.delete_node(data.reference)
      };
    }

    if(node.type != 'HBSROOT') {
      menu.rename = {
        "label": "Rinomina",
        "action": (data) => this.editNode(data.reference)
      };
    }

    return menu;

  }.bind(this);

  editNode = function(reference): void{
    let inst = this.treeRef;
    let obj = inst.get_node(reference);
    inst.edit(obj);
  }.bind(this);

  createNode = function(type, reference): void{
    this.hbsNodeType = type;
    let inst = this.treeRef;
    let obj = inst.get_node(reference);
    inst.create_node(obj, {type: type}, "last", this.editNode, true);
  }.bind(this);

  deleteNode = function(reference): void{
    let inst = this.treeRef;
    let obj = inst.get_node(reference);
    inst.delete_node(obj);
  }.bind(this);

  createDictNode = function(type, reference): void{
    let inst = this.treeRef;
    let obj = inst.get_node(reference);
    inst.create_node(obj, {type}, "last", function (new_node) {
      inst.edit(new_node, 'nuovo', this.createCV);
    }.bind(this));
  }.bind(this);

  createCV = function(new_node): void {
    let object_name = new_node.text;
    let parent_id = new_node.parent;

    jQuery.ajax({
      type: "post",
      url: this.restUrl + 'dictionary/create?cid=' + this.cid,
      data: {
        id: parent_id,
        name: object_name,
        node_type: new_node.original.type,
        generateId: true,
      },
      xhrFields: { withCredentials: true },
      success: function (response, textStatus, jqXHR) {
        new_node.id = response.id;
        new_node.type = response.type;
      },
      error: function (jqXHR, textStatus, errorThrown) {
        //jQuery.jstree.rollback(data.rlbk);
        alert(jqXHR.responseText);
      }
    });
  }.bind(this);

  isDisabled(node) {
    return node.state.disabled;
  }

  isOpened(node) {
    return node.state.opened;
  }

  isLeaf(node) {
    return this.treeRef.is_leaf(node);
  }

  nodeCanBeEnabled(node) {
//		var liElement = jQuery(node.attr('class'));
//		var liParent = jQuery(node.parent().parent().attr('class'));
//
//		if(((liElement.selector).indexOf('jstree-disabled') >= 0) && ((liParent.selector).indexOf('jstree-disabled')) == -1){
//			return true;
//		}
    return node.state.disabled; //FIXME check parent is enabled!
  }

  hasChildren(node) {
    return node.children && node.children.length > 0;
  }

  areAllChildrenDisabled(node) {
    if (!this.hasChildren(node)) {
      return this.isOpened(node) || this.isLeaf(node);
    } else {
      //FIXME check all children disabled!!!
			for(let z=0; z < node.children.length ; z++){
				if (!this.isDisabled(this.treeRef.get_node(node.children[z]))){
					return false;
				}

			}
      return true;
    }
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
  //injectOnlyLeaf: if true, allows only injection of leaf nodes
  //treeType: '' or editableCvTree (for dictionary manager)
  //selected: list of codeValues that build the PATH to the currently selected codeValue (used to pre-open the tree from root to selected node)
  //currentNode: the currently selected node
  initTreeDictionary(dictTree, codeSystem, domain, codeValueClass, dataComponent, a4jF4inject, injectIntoConversationName, injectOnlyLeaf, treeType, selected, currentNode) {
    this.cid = jQuery('#conversationId').val();
    let types = {
      ROOT: {icon: "fa fa-folder-o treeIconSize"},
      TOPLEVEL: {icon: "fa fa-folder-o treeIconSize"},
      DOMAIN: {icon: "fa fa-folder-o treeIconSize"},
      CODE: {icon: "fa fa-leaf treeIconSize"},
      PARAMETER: {icon: "fa fa-cog treeIconSize"}
    };
    this.buildTreeDictionary(dictTree, treeType, types, codeSystem, domain, codeValueClass, dataComponent, a4jF4inject, injectIntoConversationName, injectOnlyLeaf, selected, currentNode);

    this.treeRef = dictTree.jstree();
  }

  buildTreeDictionaryAngular(dictTree, treeType, types, codeSystem, domain, codeValueClass, dataComponent, a4jF4inject, injectIntoConversationName, injectOnlyLeaf, selected, currentNode) {

    if (codeSystem == null || codeSystem == "" || codeValueClass == null || codeValueClass == "") {
      return;
    }

    this.dictionaryManager.loadTree(codeSystem, domain, null, dataComponent, 1, codeValueClass).then(cvTree => {
    // let codeSystemAndDomains = codeSystem + (domain ? ':' + domain : '');
    // this.dictionaryManager.get(codeSystemAndDomains).then(cvTree => {

      this.zone.run(() => {
        const tree: TreeComponent = this.viewManager.appendComponentTo(dictTree[0], TreeComponent);
        tree.listElementsExpression = [cvTree];
        tree.expand = 1;
        tree.styleClass = 'max-height-100x100';
        tree.change.subscribe(event => {
          let a4jFtoBeCalled = new Function('id', 'codeValueClass', 'injectInto', a4jF4inject + '(id,codeValueClass,injectInto)');
          a4jFtoBeCalled.call(null, event.value.id, codeValueClass, injectIntoConversationName);
        });

      });
    });
  }

  buildTreeDictionary = function (dictTree, treeType, types, codeSystem, domain, codeValueClass, dataComponent, a4jF4inject, injectIntoConversationName, injectOnlyLeaf, selected, currentNode) {

    if (codeSystem == null || codeSystem == "" || codeValueClass == null || codeValueClass == "") {
      return;
    }

    this.codeSystem = codeSystem;

    let plugins = ["types", "search", 'massload'];
    let contextmenu = null;
    if (treeType == 'editableCvTree') {
      contextmenu = {items: this.dictionaryMenu};
      plugins.push("contextmenu");
    }

    if (selected == undefined || selected == "") {
      selected = [];
    } else {
      selected = selected.split(", ");
    }

    dictTree.jstree({
      plugins: plugins,
      contextmenu: contextmenu,
      'core': {
        'data': { //load data onclick, opening node
          type: 'post',
          url: this.restUrl+'dictionary/getTree?cid=' + this.cid,
          data: function (node) {
            if (node.id == '#') {
              console.log('get tree');
              return {
                codeSystem: codeSystem,
                domain: domain,
                dataComponent: dataComponent,
                levelOfDepth: 1,
                codeValueClass: codeValueClass
              };
            } else {
              return {
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
          url: this.restUrl + 'dictionary/search?cid=' + this.cid,
          data: {
            codeValueClass: codeValueClass
          },
          beforeSend: (jqXHR, settings) => {
            this.phi.blockUI();
          },
          error: (jqXHR, textStatus, errorThrown) => {
            this.phi.unblockUI();
          }
        }
      },
      'types': types
    });

    dictTree.bind('search.jstree', function (e, data) {
      this.phi.unblockUI();
    }.bind(this));

    dictTree.bind('loaded.jstree', function (e, data) {
      data.instance.set_state({core: {open: selected, 'selected': [currentNode]}});
    });

    // INJECT
    dictTree.bind("select_node.jstree", function (event, data) {
      if (data.node.type !== 'ROOT') {
        let id = data.node.id;
        window[a4jF4inject](id, codeValueClass, injectIntoConversationName);
      }
    });

    if (injectOnlyLeaf) {
      //INJECT ONLY LEAF
      dictTree.bind("before.jstree", function (e, data) {
        if (data.func === "select_node" && !data.inst.is_leaf(data.args[0])) {
          e.stopImmediatePropagation();
          return false;
        }
      });
    }

    dictTree.bind("delete_node.jstree", function (event, data) {
      let id = data.node.id;

      jQuery.ajax({
        type: "post",
        url: this.restUrl + 'dictionary/delete?cid=' + this.cid,
        data: { id: id },
        xhrFields: { withCredentials: true },
        error: function (jqXHR, textStatus, errorThrown) {
          //jQuery.jstree.rollback(data.rlbk);
          alert(jqXHR.responseText);
        }
      });
    }.bind(this));

  };

  dictionaryMenu = function (node) {

    let node_type = node.original.type;

    let menu: any = {};

    let createCode = {"label": "Aggiungi codice","action": (obj) => this.createDictNode('CODE', obj.reference)};
    let createDomain = {"label": "Aggiungi dominio","action": (obj) => this.createDictNode('DOMAIN', obj.reference)};
    let createTopLevelDomain = {"label": "Aggiungi dominio top level","action": (obj) => this.createDictNode('TOPLEVEL', obj.reference)};
    let createParameter = {"label": "Aggiungi parametro","action": (obj) => this.createDictNode('PARAMETER', obj.reference)};
    let deleteItem = {'label': 'Elimina', 'action': (obj) => this.deleteNode(obj.reference)};

    //control menu items
    if (node_type == 'ROOT') {
      menu.createCode = createCode;
      menu.createTopLevelDomain = createTopLevelDomain;
    }
    else if (node_type == 'TOPLEVEL') {
      if (this.codeSystem === 'ApplicationParameters') {
        menu.createParameter = createParameter;
      } else {
        menu.createCode = createCode;
      }
      menu.createDomain = createDomain;
      menu.deleteItem = deleteItem;
    }
    else if (node_type == 'DOMAIN') {
      if (this.codeSystem === 'ApplicationParameters') {
        menu.createParameter = createParameter;
      } else {
        menu.createCode = createCode;
      }
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

    let plugins = ['types', 'massload', 'checkbox', 'changed'];
    let processReadOnly = jQuery('#processReadOnly').val();

    let self = this;
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
          url: self.restUrl+'dictionary/getTree?cid=' + self.cid,
          data: function (node) {
            if (node.id == '#') {
              console.log('get tree');
              return {
                codeSystem: codeSystem,
                domain: domain,
                dataComponent: dataComponent,
                levelOfDepth: 1,
                codeValueClass: codeValueClass,
                disabled: processReadOnly
              };
            } else {
              return {
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

      if (typeof this.phi.somethingChanged !== 'undefined') {
        this.phi.somethingChanged = true;
      }

      if (data.action == 'select_node') {

        let parents = [];
        let selectedAndParentsAndChilds = [data.node.id];
        //find parents
        for (let z = 0; z < data.node.parents.length; z++) {
          let parent = data.node.parents[z];
          if (parent != "#" && data.selected.indexOf(parent) == -1) {
            parents.push(parent);
            selectedAndParentsAndChilds.push(parent);
            data.instance.select_node(parent, true);
          }
        }

        //operation
        let selectOperation = 'dictionary/select';

        //select post
        jQuery.ajax({
          type: 'post',
          url: self.restUrl + '?cid=' + self.cid,
          data: {
            operation: selectOperation,
            id: selectedAndParentsAndChilds,
            selected: data.node.id,
            parents: parents,
            codeValueClass: codeValueClass,
            listName: listName
          }
        });

        if (toReloadFunction) {
          let a4jFtoBeCalled = new Function(toReloadFunction + '()');
          this.a4jFtoBeCalled.call();
        }

      } else if (data.action == 'deselect_node') {
        let selectedAndChilds = [data.node.id];
        //find childs
        selectedAndChilds.push.apply(selectedAndChilds, data.node.children_d);
        data.instance.deselect_node(data.node.children_d, true);

        //operation
        let unselectOperation = 'dictionary/unselect';

        //unselect post
        jQuery.ajax({
          type: 'post',
          url: self.restUrl + '?cid=' + self.cid,
          data: {
            operation: unselectOperation,
            id: selectedAndChilds,
            unselected: data.node.id,
            codeValueClass: codeValueClass,
            listName: listName
          }
        });

        if (toReloadFunction) {
          let a4jFtoBeCalled = new Function(toReloadFunction + '()');
          this.a4jFtoBeCalled.call();
        }
      }
    });
  };

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
      let id = data.node.id;
      let conversationName = data.rslt.obj.attr('conversationName');
      let a4jFtoBeCalled = new Function('id', 'conversationName', 'injectAndrerenderDetail(id, conversationName)');
      a4jFtoBeCalled.call(null,id, conversationName);
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
