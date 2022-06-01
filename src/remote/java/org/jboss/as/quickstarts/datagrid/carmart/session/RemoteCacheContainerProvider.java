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

import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.commons.api.BasicCacheContainer;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.configuration.ClassAllowList;
import org.infinispan.commons.marshall.JavaSerializationMarshaller;
import org.jboss.as.quickstarts.datagrid.carmart.model.Car;

@ApplicationScoped
public class RemoteCacheContainerProvider extends CacheContainerProvider{

    private final Logger log = Logger.getLogger(this.getClass().getName());

    private BasicCacheContainer manager;

    public BasicCache<String, Object> getCache(String cacheName) {

        if (manager == null) {

            ClassAllowList allowList = new ClassAllowList();
            allowList.addClasses(Car.class);
            allowList.addClasses(java.util.ArrayList.class);
            allowList.addClasses(Car.Country.class);
            allowList.addClasses(Car.CarType.class);

            JavaSerializationMarshaller marshaller = new JavaSerializationMarshaller();
            marshaller.initialize(allowList);

            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.addServer()
                    .host("localhost")
                    .port(11222)
                    .security().authentication()
                    .username("admin")
                    .password("admin")
                    .realm("default")
                    .saslMechanism("SCRAM-SHA-512").marshaller(marshaller);

            manager = new RemoteCacheManager(builder.build());
            log.info("=== Using RemoteCacheManager (Hot Rod) ===");
        }

        return manager.getCache(cacheName);

    }

    @PreDestroy
    public void cleanUp() {
        manager.stop();
        manager = null;
    }
}
