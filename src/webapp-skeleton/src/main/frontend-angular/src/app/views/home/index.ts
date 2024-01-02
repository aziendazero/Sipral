import { HomeComponent } from './home.component';
import { BannerComponent } from './components/banner/banner.component';
import { HeaderComponent } from './components/header/header.component';
import { MenuComponent } from './components/menu/menu.component';
import { PopupComponent } from './components/popup/popup.component';
import { PopupRouterComponent } from './components/popup/popup.router.component';
import { BannerDetailsComponent } from './components/banner/components/banner-details.component';
import { PatientNoteComponent } from './components/banner/components/patient-note.component';
import { MenuItemComponent } from './components/menu/menuItem.component';
import { IframeComponent } from './components/popup/components/iframe.component';
import { AlertMessageComponent } from './components/popup/components/alert-message.component';
import { ErrorMessageComponent } from './components/popup/components/error-message.component';
import { PopupMenuComponent } from '../../widgets/menu/popup-menu.component';

export const HOME_COMPONENTS = [
  HomeComponent,
  BannerComponent,
  HeaderComponent,
  MenuComponent,
  MenuItemComponent,
  PopupComponent,
  PopupRouterComponent,
  BannerDetailsComponent,
  PatientNoteComponent,
  IframeComponent,
  AlertMessageComponent,
  ErrorMessageComponent,
  PopupMenuComponent
];

export const DIRECTIVES = [
  //PopupDirective
];
