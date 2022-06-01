/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.datagrid.carmart.session;

import org.infinispan.commons.api.BasicCache;
import org.jboss.as.quickstarts.datagrid.carmart.model.Car;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Adds, retrieves, removes new cars from the cache. Also returns a list of cars stored in the cache.
 * 
 * @author Martin Gencur
 * 
 */
@Model
public class CarManager {
    public static final String CACHE_NAME = "carcache";

    private final Logger log = Logger.getLogger(this.getClass().getName());


    @Inject
    private CacheContainerProvider provider;

    private BasicCache<String, Object> carCache;

    private String carId;
    private Car car = new Car();

    public CarManager() {
    }

    public String addNewCar() {
        carCache = provider.getCache(CACHE_NAME);
        carCache.put(CarManager.encode(car.getNumberPlate()), car);
        return "home";
    }

    @SuppressWarnings("unchecked")
    private List<String> getNumberPlateList() {

        LinkedList<String> list = new LinkedList<String>();

        for (String key : carCache.keySet()) {
            list.add(decode(key));
        }

        return list ;
    }

    public String showCarDetails(String numberPlate) {
        carCache = provider.getCache(CACHE_NAME);
        this.car = (Car) carCache.get(encode(numberPlate));
        return "showdetails";
    }

    public List<String> getCarList() {
        // retrieve a cache
        carCache = provider.getCache(CACHE_NAME);
        // retrieve a list of number plates from the cache
        return getNumberPlateList();
    }

    public String removeCar(String numberPlate) {
        log.info("deleting: " + numberPlate);
        carCache = provider.getCache(CACHE_NAME);
        carCache.remove(encode(numberPlate));
        return null;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getCarId() {
        return carId;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Car getCar() {
        return car;
    }

    public static String encode(String key) {
        try {
            return URLEncoder.encode(key, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decode(String key) {
        try {
            return URLDecoder.decode(key, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
