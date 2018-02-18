/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2018 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.stock.web;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.axelor.apps.stock.db.StockMoveLine;
import com.axelor.apps.stock.db.repo.StockMoveRepository;
import com.axelor.apps.stock.service.StockMoveService;
import com.axelor.i18n.I18n;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ProductStockController {
	
	@Inject
	private StockMoveService stockMoveService;

	public void setStockPerDay(ActionRequest request, ActionResponse response) {
	
		Context context = request.getContext();
		
		Long productId = Long.parseLong(context.get("id").toString());
		Long locationId = Long.parseLong(context.get("locationId").toString());
		LocalDate fromDate = LocalDate.parse(context.get("stockFromDate").toString());
		LocalDate toDate = LocalDate.parse(context.get("stockToDate").toString());
		
		List<Map<String,Object>> stocks = stockMoveService.getStockPerDate(locationId, productId, fromDate, toDate);
		response.setValue("$stockPerDayList", stocks);
		
	}
	
	public void displayStockMoveLine(ActionRequest request, ActionResponse response) {
		
		Context context = request.getContext();
		if (context.get("date") != null && context.getParent().get("locationId") != null) {
			LocalDate stockDate = LocalDate.parse(context.get("date").toString());
			Long locationId = Long.parseLong(context.getParent().get("locationId").toString());
			
			if (request.getContext().getParent().get("id") != null) 
				response.setView(ActionView
			            .define(I18n.get("Stock Move Lines"))
			            .model(StockMoveLine.class.getName())
						.add("grid", "stock-move-line-all-grid")
			            .add("form", "stock-move-line-all-form")
			            .domain("self.product.id = :id AND (self.stockMove.fromStockLocation.id = :locationId OR self.stockMove.toStockLocation.id = :locationId) AND self.stockMove.statusSelect != :status AND (self.stockMove.estimatedDate <= :stockDate OR self.stockMove.realDate <= :stockDate)")
			            .context("id", request.getContext().getParent().get("id"))
			            .context("locationId", locationId)
			            .context("status", StockMoveRepository.STATUS_CANCELED)
			            .context("stockDate", stockDate)
			            .map());
				response.setCanClose(true);
		}
	}
}