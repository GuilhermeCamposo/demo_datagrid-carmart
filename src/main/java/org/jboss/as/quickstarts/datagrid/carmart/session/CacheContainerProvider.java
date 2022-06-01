package org.jboss.as.quickstarts.datagrid.carmart.session;

import org.infinispan.commons.api.BasicCache;

public abstract class CacheContainerProvider  {

    abstract public BasicCache<String, Object> getCache(String cacheName);

}
