package org.jboss.resteasy.client.core.executors;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethodBase;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.RuntimeDelegate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class HttpClientHeaderWrapper implements MultivaluedMap<String, Object>
{

   private CaseInsensitiveMap cachedHeaders = new CaseInsensitiveMap();
   private HttpMethodBase httpMethod;
   private ResteasyProviderFactory factory;

   public HttpClientHeaderWrapper(HttpMethodBase httpMethod, ResteasyProviderFactory factory)
   {
      this.httpMethod = httpMethod;
      this.factory = factory;
   }

   public void sync()
   {
      for (Header header : httpMethod.getRequestHeaders())
      {
         cachedHeaders.add(header.getName(), header.getValue());
      }
   }

   public void putSingle(String key, Object value)
   {
      cachedHeaders.putSingle(key, value);
      addResponseHeader(key, value);
   }

   public void add(String key, Object value)
   {
      cachedHeaders.add(key, value);
      addResponseHeader(key, value);
   }

   protected void addResponseHeader(String key, Object value)
   {
      RuntimeDelegate.HeaderDelegate delegate = factory.createHeaderDelegate(value.getClass());
      if (delegate != null)
      {
         //System.out.println("addResponseHeader: " + key + " " + delegate.toString(value));
         httpMethod.addRequestHeader(key.toLowerCase(), delegate.toString(value));
      }
      else
      {
         //System.out.println("addResponseHeader: " + key + " " + value.toString());
         httpMethod.addRequestHeader(key.toLowerCase(), value.toString());
      }
   }

   public Object getFirst(String key)
   {
      return cachedHeaders.getFirst(key);
   }

   public int size()
   {
      return cachedHeaders.size();
   }

   public boolean isEmpty()
   {
      return cachedHeaders.isEmpty();
   }

   public boolean containsKey(Object o)
   {
      return cachedHeaders.containsKey(o);
   }

   public boolean containsValue(Object o)
   {
      return cachedHeaders.containsValue(o);
   }

   public List<Object> get(Object o)
   {
      return cachedHeaders.get(o);
   }

   public List<Object> put(String s, List<Object> objs)
   {
      for (Object obj : objs)
      {
         addResponseHeader(s, obj);
      }
      return cachedHeaders.put(s, objs);
   }

   public List<Object> remove(Object o)
   {
      throw new RuntimeException("Removing a header is illegal for an HttpServletResponse");
   }

   public void putAll(Map<? extends String, ? extends List<Object>> map)
   {
      for (String key : map.keySet())
      {
         List<Object> objs = map.get(key);
         for (Object obj : objs)
         {
            add(key, obj);
         }
      }
   }

   public void clear()
   {
      throw new RuntimeException("Removing a header is illegal for an HttpServletResponse");
   }

   public Set<String> keySet()
   {
      return cachedHeaders.keySet();
   }

   public Collection<List<Object>> values()
   {
      return cachedHeaders.values();
   }

   public Set<Entry<String, List<Object>>> entrySet()
   {
      return cachedHeaders.entrySet();
   }

   public boolean equals(Object o)
   {
      return cachedHeaders.equals(o);
   }

   public int hashCode()
   {
      return cachedHeaders.hashCode();
   }
}