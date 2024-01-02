import { ChangeDetectionStrategy, Component } from '@angular/core';
import { HttpService } from '../../services/http.service';
/**
 * Created by 510537 on 30/08/2017.
 */

@Component({
  selector: 'phi-loader',
  changeDetection: ChangeDetectionStrategy.Default,
  templateUrl: './loader.component.html',
  styleUrls: ['./loader.component.scss']
})
export class LoaderComponent {
  constructor(
    public httpService: HttpService
  ) {

  }
}
