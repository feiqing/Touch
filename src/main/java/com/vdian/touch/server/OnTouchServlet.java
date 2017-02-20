package com.vdian.touch.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.vdian.touch.constant.Constant;
import com.vdian.touch.exceptions.TouchException;
import com.vdian.touch.helper.ConverterHelper;
import com.vdian.touch.helper.SwitcherHelper;
import com.vdian.touch.helper.TouchContextHelper;
import com.vdian.touch.utils.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author jifang
 * @since 16/10/20 下午1:56.
 */
@WebServlet(
        name = "OnTouchServlet",
        urlPatterns = "/touch/*",
        displayName = "OnTouchServlet"
)
public class OnTouchServlet extends HttpServlet {

    private TouchContextHelper touchContextHelper;

    private SwitcherHelper switcherHelper;

    private ConverterHelper converterHelper;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(Constant.CONFIG_FILE);
        ConfigEntity config = ConfigParseUtil.parseConfigFile(is);

        // save packages
        getServletContext().setAttribute(Constant.PACKAGES, config.getPackages());

        // init touch context
        touchContextHelper = new TouchContextHelper(getServletContext()).init();

        // init switcher context
        Map<String, Map<String, String>> switcherMap = config.getSwitcherMap();
        switcherHelper = new SwitcherHelper(getServletContext()).init(switcherMap);

        // init converter context
        Collection<String> converters = config.getConverters();
        converterHelper = new ConverterHelper(getServletContext()).init(converters);
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String touchPattern = BaseUtils.getTouchPattern(req.getRequestURI());
        String queryString = BaseUtils.getQueryString(req.getQueryString());

        if (switcherHelper.isSwitchPassed(touchPattern, queryString)) {

            if (touchPattern.equals("lists.do")) {
                String patternJsonStr = JSON.toJSONString(touchContextHelper.listPatterns());
                resp.getWriter().println(patternJsonStr);
            } else {
                TouchEntity touchEntity = touchContextHelper.getContextEntity(touchPattern);

                try {
                    Object instance = touchEntity.getInstance();
                    AccessibleObject accessible = touchEntity.getAccessibleObject();
                    List<MethodEntity> entities = touchEntity.getMethodEntities();

                    String jsonResult;
                    if (accessible instanceof Method) {
                        Object[] args = ArgComposeUtil.composeArgs(queryString, entities, converterHelper);

                        Object result = ((Method) accessible).invoke(instance, args);
                        jsonResult = JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
                    } else {
                        JSONObject json = new JSONObject(2);
                        Field touchFiled = (Field) accessible;
                        json.put("old", BaseUtils.getFieldValue(touchFiled, instance));
                        touchFiled.set(instance, ArgComposeUtil.composeField(queryString, entities, converterHelper));
                        json.put("new", BaseUtils.getFieldValue(touchFiled, instance));

                        jsonResult = json.toJSONString();
                    }
                    resp.getWriter().println(jsonResult);
                } catch (Exception e) {
                    throw new TouchException(e);
                }
            }
        } else {
            resp.getWriter().println("<h1>Please Contact Touch Admin to Open Touch Switcher</h1>");
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        this.doGet(req, resp);
    }
}
