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
package org.jboss.as.quickstarts.datagrid.carmart.jsf;

import java.util.logging.Logger;
import javax.faces.application.Application;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.infinispan.commons.api.BasicCache;
import org.jboss.as.quickstarts.datagrid.carmart.model.Car;
import org.jboss.as.quickstarts.datagrid.carmart.model.Car.CarType;
import org.jboss.as.quickstarts.datagrid.carmart.model.Car.Country;
import org.jboss.as.quickstarts.datagrid.carmart.session.CacheContainerProvider;
import org.jboss.as.quickstarts.datagrid.carmart.session.CarManager;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * Populates a cache with initial data. We need to obtain BeanManager from JNDI and create an instance of CacheContainerProvider
 * manually since injection into Listeners is not supported by CDI specification.
 * 
 * @author Martin Gencur
 * 
 */
public class PopulateCache implements SystemEventListener {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    private CacheContainerProvider provider;

    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        provider = getContextualInstance(getBeanManager(), CacheContainerProvider.class);
        startup();
    }

    public void startup() {
        BasicCache<String, Object> cars = provider.getCache(CarManager.CACHE_NAME);

        try {

            if(cars.isEmpty()){
                Car c = new Car("Ford Focus", 1.6, CarType.COMBI, "white", "FML 23-25", Country.CZECH_REPUBLIC);
                cars.put(CarManager.encode(c.getNumberPlate()), c);
                c = new Car("BMW X3", 2.0, CarType.SEDAN, "gray", "1P3 2632", Country.CZECH_REPUBLIC);
                cars.put(CarManager.encode(c.getNumberPlate()), c);
                c = new Car("Ford Mondeo", 2.2, CarType.COMBI, "blue", "1B2 1111", Country.USA);
                cars.put(CarManager.encode(c.getNumberPlate()), c);
                c = new Car("Mazda MX-5", 1.8, CarType.CABRIO, "red", "6T4 2526", Country.USA);
                cars.put(CarManager.encode(c.getNumberPlate()), c);
                c = new Car("VW Golf", 1.6, CarType.HATCHBACK, "yellow", "2B2 4946", Country.GERMANY);
                cars.put(CarManager.encode(c.getNumberPlate()), c);

                log.info("Successfully imported data!");
            }else{
                log.info("Data already present");
            }

        } catch (Exception e) {
            log.severe(e.getMessage());
        }

    }

    private BeanManager getBeanManager() {
        // BeanManager is only available when CDI is available. This is achieved by the presence of beans.xml file
        InitialContext context;
        Object result;
        try {
            context = new InitialContext();
            result = context.lookup("java:comp/env/BeanManager"); // lookup in Tomcat
        } catch (NamingException e) {
            try {
                context = new InitialContext();
                result = context.lookup("java:comp/BeanManager"); // lookup in JBossAS
            } catch (NamingException ex) {
                throw new RuntimeException("BeanManager could not be found in JNDI", e);
            }
        }
        return (BeanManager) result;
    }

    @SuppressWarnings("unchecked")
    public <T> T getContextualInstance(final BeanManager manager, final Class<T> type) {
        T result = null;
        Bean<T> bean = (Bean<T>) manager.resolve(manager.getBeans(type));
        if (bean != null) {
            CreationalContext<T> context = manager.createCreationalContext(bean);
            if (context != null) {
                result = (T) manager.getReference(bean, type, context);
            }
        }
        return result;
    }

    @Override
    public boolean isListenerForSource(Object source) {
        return source instanceof Application;
    }
}
