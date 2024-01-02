import { Injectable, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { select } from '@angular-redux/store';
import { ConversationActions } from '../actions/conversation.actions';
import { ConfigActions } from '../actions/config.action';
import { Config } from '../store/config.reducer';
import { SelectItem } from './datamodel/select-item';
import { Employee } from './entities/role/employee';
import { EmployeeRole } from './entities/base-entity/employee-role';
import { HttpService } from './http.service';
import { BannerActions } from '../actions/banner.action';
import { ProcessActions, ReloginResponse } from '../actions/process.actions';
import { TranslateService } from '@ngx-translate/core';
import { anyToDate } from './converters/date.converter';
import { AdviseMsgActionService } from './actions/advise-msg-action.service';
import { environment } from 'environments/environment';
import { reviver } from './converters/any.converter';

@Injectable()
export class AuthGuard {

  @select(['config']) config$;
  config: Config;

  @select(['process', 'lang']) lang$;
  lang;

  // FIXME REMOVE ALL AND USE ONLY CONFIG
  employee: Employee;
  employeeRoleCode: string;
  sid: string;


  constructor(
    @Inject('apiUrl') private url,
    private router: Router,
    private configActions: ConfigActions,
    private conversationActions: ConversationActions,
    private bannerActions: BannerActions,
    private processActions: ProcessActions,
    private httpService: HttpService,
    private translateService: TranslateService,
    private adviseMsgAction: AdviseMsgActionService
  ) {

    this.config$.subscribe((cfg: Config) => {
      this.config = cfg;

      this.employee = cfg.employee;
      this.employeeRoleCode = cfg.employeeRoleCode;
      this.sid = cfg.sid;
    });

    this.lang$.subscribe(res => this.lang = res);
  }

  /**
   * Check authentication status from login page
   * @param route
   * @param state
   * @returns {Promise<boolean>|boolean}
   */
  /*
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> | boolean {
    console.log('APP ROUTER: Trying to route to ' + route.url[0].path + ' [canActivate] - called by app routing');

    return this.isAuthenticated().then((auth) => {
      if (auth && !this.config.passwordExpired && !this.config.options) {
        this.router.navigate(['/']);
        return false;
      }
      return true;
    });

  }
  */

  /**
   * Check authentication status from home page
   * @param route
   * @param state
   * @returns {Promise<boolean>|boolean}
   */
  /*
  canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> | boolean {
    console.log('APP ROUTER: Trying to route to ' + route.url[0].path + ' [canActivateChild] - called by home routing');

    return this.isAuthenticated().then((auth) => {
      if (auth && !this.config.passwordExpired && !this.config.options) {
        return true;
      } else {
        this.router.navigate(['/login']);
        return false;
      }
    });

  }
  */

  /**
   * Check authentication state
   * @returns {any}
   */
  public isAuthenticated(): Promise<boolean> {
    if (this.config.employee != null) {
      return Promise.resolve(true);
    } else {
      return this.checkIsAuthenticated();
    }
  }

  public checkIsAuthenticated(): Promise<boolean> {
    return this.httpService.fetch(this.url + 'authentication',
      {
        method: 'GET',
        credentials: 'include'
      }
    )
      .then(response => response.json())
      .then(json => this.parseResponse(json))
      .catch(() => {
        return false;
      });
  }

  // Popup sei sicuro?
  canDeactivate(): Promise<boolean> | boolean {
    // // Allow users to exit from view if user is not editing
    // if (!this.isEditing) {
    //   return true;
    // }
    // // Otherwise ask the user what to do, and return a
    // // promise which resolves to true or false when the user decides
    // return this.dialog.show('Discard changes?');
    return true;
  }


  /**
   * Login
   * @param username
   * @param password
   * @returns {any|Promise<T>}
   */
  login(username: string, password: string) {

    return this.httpService.fetch(this.url + 'authentication' + ';jsessionid=' + this.config.sid + '?_=' + new Date().getTime(),
      // + '?conversationId=' + this.apiService.cid,
      {
        method: 'POST',
        body: 'username=' + encodeURIComponent(username) + '&password=' + encodeURIComponent(password),
        headers: new Headers({'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}),
        credentials: 'include'
      }
    )
      .then(response => {
        return response.json().then((json) => {
          if (!json.error) {
            const logged = this.parseResponse(json);
            if (logged) {
              this.configActions.put('errors', []);
              if (this.config.options === false) { // one role and one structure (no options needed)
                const now = new Date().getTime();
                const ers: Array<EmployeeRole> = this.config.employee.employeeRole.filter((er: EmployeeRole) => {
                  if (er.effectiveTime.low) {
                    const date = anyToDate(er.effectiveTime.low);
                    if (date instanceof Date && date.getTime() > now) {
                      return false;
                    }
                  }
                  if (er.effectiveTime.high) {
                    const date = anyToDate(er.effectiveTime.high);
                    if (date instanceof Date && date.getTime() < now) {
                      return false;
                    }
                  }
                  return true;
                })
                const defaultEr: Array<EmployeeRole> = ers.filter((er: EmployeeRole) => {
                  if (er.defaultRole) {
                    return true;
                  } else {
                    return false;
                  }
                });
                this.setOptions(defaultEr && defaultEr.length === 1 ? defaultEr[0].internalId : ers[0].internalId, null, this.lang);
              } else {
                this.router.navigate(['/login/options']);
              }
            }
          } else {
            this.configActions.put('errors', [json.error]);
          }
        });
      }).catch(error => {
        console.error('Error login ' + username + ' ' + error.message);
        throw error;
      });

  }

  /**
   * Logout
   * @returns {any|Promise<T>}
   */
  logout() {

    return this.httpService.fetch(this.url + 'authentication' + ';jsessionid=' + this.config.sid,
      {
        method: 'DELETE',
        credentials: 'include'
      }
    )
      .then(() => {
        this.conversationActions.clean();
        this.configActions.clean();
        this.processActions.clean();
        this.bannerActions.remove();
        this.router.navigate(['/login']);
      })
      .catch(error => {
        console.error('Error logout ' + ' ' + error.message);
        throw error;
      });
  }

  checkRelogin(isAdmin = false) {
    if (isAdmin || environment.lockedNodeProcess === undefined) {
      return Promise.resolve({
            reloginUrl: null,
            isLockedNode: null
          }
      );
    } else {
      return this.httpService.fetch(
        this.url + 'resource/rest/' + 'sessionUtilities/reloginCheck',
        {
          method: 'GET',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include'
        }
      )
        .then(response => response.text())
        .then(text => JSON.parse(text, reviver))
        .then(reloginUrl => {

          if (reloginUrl) {
            let absoluteReloginUrl = reloginUrl;
            if (absoluteReloginUrl.indexOf('/') === 0) {
              absoluteReloginUrl = absoluteReloginUrl.substring(1, absoluteReloginUrl.length);
            }

            return {
              reloginUrl: this.url + absoluteReloginUrl,
              isLockedNode: (absoluteReloginUrl.indexOf('isLockedNode') > -1)
            };
          } else {
            return {
              reloginUrl: null,
              isLockedNode: null
            };
          }
        })
        .catch(error => {
          console.error('Error checking relogin ' + error.message);
          throw error;
        });
    }
  }

  /**
   * If current cluster node needRelogin, logout and refresh page,
   * used to switch cluster node
   * @param resp 
   */
  needRelogin(resp: ReloginResponse) {
    if (resp.reloginUrl) {
      if (resp.isLockedNode && environment.lockedNodeProcess) {
        this.configActions.put('isLockedNode', true);
        this.processActions.startProcess(environment.lockedNodeProcess);
      } else {
        return this.logout()
        .then(() => {
          document.open();
          document.close();
          let reloginUrl = resp.reloginUrl;
          window.location.href = reloginUrl;
        });
      }
    }
  }

  /**
   * Get enabled ServiceDeliveryLocations for the selected role
   * @param selectedRole
   * @returns {any|Promise<TResult>}
   */
  loadSdlocs(selectedRole): Promise<any> {
    // return fetch(this.url + 'ajax' + ';jsessionid=' + this.config.sid,
    return this.httpService.fetch(this.url + 'resource/rest/tree/hbs/getTree4CurrentUser' + ';jsessionid=' + this.config.sid,
      {
        method: 'POST',
        body: 'selectedRole=' + selectedRole,
        headers: new Headers({'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}),
        credentials: 'include'
      })
      .then(response => response.json())
      .then(hbsTree => this.parseSdlocs(hbsTree, null));
  }

  parseSdlocs(hbsTree, selectedTree: Array<number>) {
    if (hbsTree) {

      let filterTree = (children) => {
        let fChildren = children.filter(sdl => sdl && sdl.state && !sdl.state.disabled);
        return fChildren.map(sdl => {
          sdl.children = filterTree(sdl.children || []);
          return sdl;
        });
      };
      hbsTree = filterTree(hbsTree); // REMOVES DISABLED SDL FROM HBS TREE

      let wards = false;
      let uds = false;

      let flatten = (children, parent) => {
        let filteredChildren = children.filter(sdl => sdl && sdl.state && !sdl.state.disabled);
        return Array.prototype.concat.apply(
          filteredChildren,
          filteredChildren.map( sdl => {
          if (sdl.type === 'WARD') {
            wards = true;
          }
          if (sdl.type === 'UD') {
            uds = true;
          }
          sdl.parent = parent;
          return flatten(sdl.children || [], sdl)
        })
      )};

      let flat = flatten(hbsTree, null);

      /*if (wards) {
        if (!uds) {
          this.configActions.put('area', 'ci');
        }
      } else {
        if (uds) {
          this.configActions.put('area', 'amb');
        }
      }*/
      if (selectedTree) {
        // filter selected sdl
        flat = flat.filter(sdl =>
          selectedTree.indexOf(+sdl.id) !== -1
        );
      }
      this.configActions.put('sdLocs', flat);
    }

    this.configActions.put('enabledSdlocs', hbsTree);
    return hbsTree;
  }

  public setOptions(selectedRole: number, sdlocs, lang: string): Promise<any> {

    let body = JSON.stringify({selectedRole, sdlocs, lang});

    return this.httpService.fetch(this.url + 'authentication' + ';jsessionid=' + this.config.sid,
      {
        method: 'PUT',
        body: body,
        headers: new Headers({'Content-Type': 'application/json; charset=utf-8'}),
        credentials: 'include'
      })
      .then(response => {
        return response.json().then((json) => {
          if (!json.error) {
            if (json['currentEmployee']) { // if logged
              this.configActions.put('errors', []);
              this.configActions.putOptions(false);
              this.configActions.putParam(json['param'] || {} as Map<string, any>);
              this.configActions.putEmployeeRoleCode(json['roleCode']);
              this.configActions.put('employeeRole', json['role']);
              this.configActions.put('employeeRoleId', selectedRole);
              this.router.navigate(['/']);
              return true;
            }
          } else {
            this.configActions.put('errors', [json.error]);
          }
        });
      })
      .catch(error => {
        console.error('Error setting options ' + error.message);
        throw error;
      });

  }

  private parseResponse(json): boolean {

    this.configActions.putSid(json['sid']);

    if (!json['currentEmployee']) {

      this.configActions.clean();
      return false;

    } else {

      const employee: Employee = json['currentEmployee'];
      this.configActions.putEmployee(employee);

      this.parseSdlocs(JSON.parse(json['sdLocs']), json['selectedSdLocs']); // FIXME: remove double serialization!
      // this.parseSdlocs(json['sdLocs']);
      this.configActions.putParam(json['param'] || {} as Map<string, any>);
      this.configActions.putEmployeeRoleCode(json['roleCode']);

      const now = new Date().getTime();
      let ers: Array<EmployeeRole> = (this.employee.employeeRole.filter((er: EmployeeRole) => {
        if (er.effectiveTime.low) {
          const date = anyToDate(er.effectiveTime.low);
          if (date instanceof Date && date.getTime() > now) {
            return false;
          }
        }
        if (er.effectiveTime.high) {
          const date = anyToDate(er.effectiveTime.high);
          if (date instanceof Date && date.getTime() < now) {
            return false;
          }
        }
        return er.code.code === this.employeeRoleCode
      }));

      let key = 'currentTranslation';

      if (json['language']) {

        const supportedLang = environment.languages.find(lang =>
          lang[0] === json['language']
        );

        if (supportedLang) {
          this.processActions.setLanguage(json['language']);
          this.translateService.use(json['language']);

          key = 'lang' + json['language'].substr(0, 1).toUpperCase() + json['language'].substr(1, 1).toLowerCase();
        }
      }

      if (ers.length > 0) {
        this.configActions.put('employeeRole', ers[0].code[key] ? ers[0].code[key] : ers[0].code['currentTranslation']);
      } else {
        this.configActions.put('employeeRole', json['role']);
      }
      this.configActions.put('employeeRoleId', json['employeeRoleId']);
      this.configActions.putPasswordExpired(json['passwordExpired']);
      this.configActions.putOptions(json['loginOptions']);

      if (employee && employee.employeeRole) {
        const enabledRoles: Array<SelectItem> = employee.employeeRole
          .filter((er: EmployeeRole) => {
            if (er.effectiveTime.low) {
              const date = anyToDate(er.effectiveTime.low);
              if (date instanceof Date && date.getTime() > now) {
                return false;
              }
            }
            if (er.effectiveTime.high) {
              const date = anyToDate(er.effectiveTime.high);
              if (date instanceof Date &&  date.getTime() < now) {
                return false;
              }
            }
            return true;
          })
          .map((er: EmployeeRole) => {
          return {
            label: ((er.code[key] ? er.code[key] : er.code['currentTranslation']) + ((environment.options.showComment && er.loginComment) ? (' - ' + er.loginComment) : '')),
            value: er.internalId,
            effectiveTime: er.effectiveTime.low
          }
        }).sort((a, b) => {
          let nameA = a.label; // ignore upper and lowercase
          let nameB = b.label; // ignore upper and lowercase
          if (nameA < nameB) {
            return -1;
          } else if (nameA > nameB) {
            return 1;
          } else {
            let etA = a.effectiveTime;
            let etB = b.effectiveTime;
            if (etA > etB) {
              return -1;
            } else if (etA < etB) {
              return 1;
            }
          }
        });
        this.configActions.put('enabledRoles', enabledRoles);
      }

      if (json['message']) {
        this.configActions.put('alertMessage', {
          title: '',
          message: json['message'],
          showCancel: false,
          showOk: false,
          okGoesBack: true,
          cancelGoesBack: true,
          modal: false,
          onCancel: () => this.adviseMsgAction.setAsRead()
        });
      }

      if (json['isLoggedByServlet']) {
        this.configActions.put('isLoggedByServlet', true);
      }

      if (json['isLockedNode']) {
        this.configActions.put('isLockedNode', true);
      }

      return true;
    }
  }

}
