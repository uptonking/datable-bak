package com.tripfinger.commons.prost;

import com.tripfinger.commons.prost.annotations.RestMethod;
import com.tripfinger.commons.prost.model.Authorizer;
import com.tripfinger.commons.prost.model.HttpMethod;
import com.tripfinger.commons.prost.model.HttpResponse;
import com.tripfinger.commons.prost.utils.Tuple;
import com.tripfinger.commons.prost.annotations.Guard;
import com.tripfinger.commons.prost.annotations.Open;
import com.tripfinger.commons.prost.annotations.UrlParam;
import com.tripfinger.commons.prost.utils.StreamUtils;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RequestHandler extends HttpServlet {

  protected static Authorizer authorizer = null;

  protected static Map<String, Tuple<PathEntry, Map>> restHandlers = new HashMap<>();
  protected static boolean guardAll = false;
  protected static Set<HttpMethod> guardedMethods = new HashSet<>();

  public void setAuthorizer(Authorizer authorizer) {
    RequestHandler.authorizer = authorizer;
  }

  public void addMethodGuard(HttpMethod httpMethod) {
    guardedMethods.add(httpMethod);
  }

  public void setRestHandler(Class restHandler) {

    guardAll = restHandler.isAnnotationPresent(Guard.class);

    for(Method m: restHandler.getDeclaredMethods()) {
      if (m.isAnnotationPresent(RestMethod.class)) {
        RestMethod annotation = m.getAnnotation(RestMethod.class);
        String url = annotation.value();
        PathEntry pathEntry = new PathEntry();
        MethodEntry methodEntry = new MethodEntry();
        methodEntry.method = m;
        if (!guardAll && m.isAnnotationPresent(Guard.class)) {
          methodEntry.guardStatus = GuardStatus.GUARDED;
        }
        else if (m.isAnnotationPresent(Open.class)) {
          methodEntry.guardStatus = GuardStatus.OPEN;
        }
        else {
          methodEntry.guardStatus = GuardStatus.NOT_SPECIFIED;
        }
        pathEntry.methods.put(annotation.method(), methodEntry);
        pathEntry.urlParams = getUrlParamsForMethod(m);
        compileUrlParts(getUrlParts(url), restHandlers, pathEntry);
      }
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String url = req.getPathInfo();
    HttpMethod httpMethod = HttpMethod.valueOf(req.getMethod());
    Map<String, String[]> parameterMap = req.getParameterMap();
    Map<String, String> parameters = new HashMap<>();
    for (Map.Entry<String, String[]> parameter : parameterMap.entrySet()) {
      parameters.put(parameter.getKey(), parameter.getValue()[0]);
    }
    writeResponse(resp, handleRequest(url, httpMethod, null, parameters, null));
  }

  @Override
  protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    setCorsHeaders(resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    String body = null;
    Map<String, byte[]> items = new HashMap<>();
    if (ServletFileUpload.isMultipartContent(req)) {

      try {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(100_000_000);
        ServletFileUpload upload = new ServletFileUpload(factory);
        FileItemIterator iterator = upload.getItemIterator(req);
        while (iterator.hasNext()) {
          FileItemStream item = iterator.next();
          String name = item.getFieldName();

          if (!item.isFormField()) {
            items.put(name, StreamUtils.readBytesFromInputStream(item.openStream()));
          }
        }
      }
      catch (FileUploadException e) {
        throw new RuntimeException(e);
      }
    }
    else {
      body = getPostBodyForRequest(req);
    }


    String url = req.getPathInfo();
    HttpMethod httpMethod = HttpMethod.valueOf(req.getMethod());
    Map<String, String> parameters = new HashMap<>();
    writeResponse(resp, handleRequest(url, httpMethod, body, parameters, items));
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doGet(req, resp);
  }

  protected void setCorsHeaders(HttpServletResponse resp) {
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
    resp.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Authorization");
  }

  protected void writeResponse(HttpServletResponse resp, HttpResponse response) throws IOException {
    resp.setContentType(response.contentType);
    resp.setStatus(response.status);
    resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
    setCorsHeaders(resp);

    resp.getWriter().println(response.body);
  }

  public HttpResponse handleRequest(String url, HttpMethod httpMethod, String requestBody, Map<String, String> parameters,
                                    Map<String, byte[]> files) {
    List<String> urlParts = getUrlParts(url);

    if (urlParts.size() >= 1) {

      return handleRequest(httpMethod, urlParts, parameters, requestBody, files);
    }
    else {
      return new HttpResponse(404, "URL not valid: " + url);
    }
  }

  public String getPostBodyForRequest(HttpServletRequest req) {
    try (InputStream input = req.getInputStream()) {
      return StreamUtils.readStringFromInputStream(input);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected List<String> getUrlParts(String url) {
    return Arrays.asList(url.replaceFirst("^/", "").split("/"));
  }

  protected enum GuardStatus {
    NOT_SPECIFIED,
    GUARDED,
    OPEN
  }

  protected class MethodEntry {
    public Method method;
    public GuardStatus guardStatus;
  }


  protected class PathEntry {
    public Map<HttpMethod, MethodEntry> methods = new HashMap<>();
    public List<String> urlParams = new LinkedList<>();
  }

  protected void compileUrlParts(List<String> urlParts, Map<String, Tuple<PathEntry, Map>> handlers, PathEntry m) {
    String urlPart = urlParts.get(0);
    if (urlPart.startsWith(":")) {
      urlPart = "param";
    }
    Tuple<PathEntry, Map> entry = handlers.get(urlPart);
    if (entry == null) {
      entry = new Tuple<>(null, null);
      handlers.put(urlPart, entry);
    }
    if (urlParts.size() == 1) {
      if (entry.x != null) {
        for (Map.Entry<HttpMethod, MethodEntry> method : m.methods.entrySet()) {
          entry.x.methods.put(method.getKey(), method.getValue());
        }
        entry.x.urlParams = m.urlParams;
      }
      else {
        entry.x = m;
      }
    }
    else {
      if (entry.y == null) {
        entry.y = new HashMap<Method, Tuple<Method, Map>>();
      }
      compileUrlParts(urlParts.subList(1, urlParts.size()), entry.y, m);
    }
  }

  protected List<String> getUrlParamsForMethod(Method m) {
    List<String> urlParams = new LinkedList<>();
    Annotation[][] annotationArrays = m.getParameterAnnotations();
    for (Annotation[] parameterAnnotations : annotationArrays) {
      if (parameterAnnotations.length > 0) {
        UrlParam urlParam = (UrlParam)parameterAnnotations[0];
        urlParams.add(urlParam.value());
      }
    }
    return urlParams;
  }

  protected PathEntry getUrlMethod(List<String> pathElements, List<Object> restParameters,
                                          Map<String, Tuple<PathEntry, Map>> handlers,
                                          HttpMethod method) {
    String pathElement = pathElements.get(0);
    Tuple<PathEntry, Map> element = handlers.get(pathElement);
    boolean addParameter = false;
    if (element == null) {
      addParameter = true;
      element = handlers.get("param");
      if (element == null) {
        return null;
      }
    }
    if (pathElements.size() == 1) {
      if (element.x == null) {
        addParameter = true;
        element = handlers.get("param");
      }
      if (!element.x.methods.containsKey(method)) {
        return null;
      }
      if (addParameter) {
        restParameters.add(pathElement);
      }
      return element.x;
    }
    else {
      if (element.y == null) {
        return null;
      }
      if (addParameter) {
        restParameters.add(pathElement);
      }
      PathEntry entry = getUrlMethod(pathElements.subList(1, pathElements.size()), restParameters, element.y, method);
      if (entry == null && addParameter) {
        restParameters.remove(restParameters.size() - 1);
      }
      if (entry == null && !pathElement.equals("param")) {
        restParameters.add(pathElement);
        List<String> newPathElements  = new LinkedList<>();
        newPathElements.add("param");
        newPathElements.addAll(pathElements.subList(1, pathElements.size()));
        entry = getUrlMethod(newPathElements, restParameters, handlers, method);
        if (entry == null) {
          restParameters.remove(restParameters.size() - 1);
        }
      }
      return entry;
    }
  }

  public HttpResponse handleRequest(List<String> pathElements, Map<String, String> parameters) {
    return handleRequest(HttpMethod.GET, pathElements, parameters, null, null);
  }

  public HttpResponse handleRequest(List<String> pathElements, String body) {
    return handleRequest(HttpMethod.POST, pathElements, null, body, null);
  }

  public HttpResponse handleRequest(HttpMethod httpMethod, List<String> pathElements, Map<String, String> parameters, String body, Map<String, byte[]> files) {


    List<Object> restParameters = new LinkedList<>();
    PathEntry pathEntry = getUrlMethod(pathElements, restParameters, restHandlers, httpMethod);
    if (pathEntry == null || !pathEntry.methods.containsKey(httpMethod)) {
      return new HttpResponse(404, "Resource not found: " + pathElements);
    }

    MethodEntry m = pathEntry.methods.get(httpMethod);
    if (m.guardStatus != GuardStatus.OPEN &&
        (guardAll || guardedMethods.contains(httpMethod) || m.guardStatus == GuardStatus.GUARDED)) {
      if (authorizer == null) {
        throw new RuntimeException("Method guarded but authorizer not set.");
      }

      if (!authorizer.isAuthorized()) {
        return new HttpResponse(401, "Method call was not authorized.");
      }
    }

    try {
      if (httpMethod != HttpMethod.POST) {
        for (String urlParameter : pathEntry.urlParams) {
          restParameters.add(parameters.get(urlParameter));
        }
      }
      else {
        restParameters.add(body);

        if (files != null && files.size() > 0) {
          restParameters.add(files);
        }
      }
      HttpResponse response = (HttpResponse)m.method.invoke(null, restParameters.toArray());
      if (response == null) {
        return new HttpResponse(404, "Resource not found: " + pathElements);
      }
      return response;
    }
    catch (Exception e) {
      Throwable inner = e;
      if (InvocationTargetException.class.isInstance(e)) {
        inner = e.getCause();
      }
      StringWriter errors = new StringWriter();
      inner.printStackTrace(new PrintWriter(errors));
      System.out.println(errors.toString());
      return new HttpResponse(500, inner.toString());
    }
  }
}