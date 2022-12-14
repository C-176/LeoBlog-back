package com.chen.LeoBlog.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BaseConstant {
    @Value("${server.port}")
    private static int port;

    public static final String PATH_PREFIX = "http://localhost:"+port+"/";

    public static final String htmlPrefix = "<!DOCTYPE html>\n" +
            "<html lang=\"zh\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "\n" +
            "    <base target=\"_blank\"/>\n" +
            "    <style type=\"text/css\">::-webkit-scrollbar {\n" +
            "        display: none;\n" +
            "    }</style>\n" +
            "    <style id=\"cloudAttachStyle\" type=\"text/css\">#divNeteaseBigAttach, #divNeteaseBigAttach_bak {\n" +
            "        display: none;\n" +
            "    }</style>\n" +
            "    <style id=\"blockquoteStyle\" type=\"text/css\">blockquote {\n" +
            "        display: none;\n" +
            "    }</style>\n" +
            "    <style type=\"text/css\">\n" +
            "        body {\n" +
            "            font-size: 14px;\n" +
            "            font-family: arial, verdana, sans-serif;\n" +
            "            line-height: 1.666;\n" +
            "            padding: 0;\n" +
            "            margin: 0;\n" +
            "            overflow: auto;\n" +
            "            white-space: normal;\n" +
            "            word-wrap: break-word;\n" +
            "            min-height: 100px\n" +
            "        }\n" +
            "\n" +
            "        td, input, button, select, body {\n" +
            "            font-family: Helvetica, 'Microsoft Yahei', verdana\n" +
            "        }\n" +
            "\n" +
            "        pre {\n" +
            "            white-space: pre-wrap;\n" +
            "            white-space: -moz-pre-wrap;\n" +
            "            white-space: -pre-wrap;\n" +
            "            white-space: -o-pre-wrap;\n" +
            "            word-wrap: break-word;\n" +
            "            width: 95%\n" +
            "        }\n" +
            "\n" +
            "        th, td {\n" +
            "            font-family: arial, verdana, sans-serif;\n" +
            "            line-height: 1.666\n" +
            "        }\n" +
            "\n" +
            "        img {\n" +
            "            border: 0\n" +
            "        }\n" +
            "\n" +
            "        header, footer, section, aside, article, nav, hgroup, figure, figcaption {\n" +
            "            display: block\n" +
            "        }\n" +
            "\n" +
            "        blockquote {\n" +
            "            margin-right: 0px\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body tabindex=\"0\" role=\"listitem\">\n" +
            "\n" +
            "\n" +
            "<table width=\"700\" border=\"0\" align=\"center\" cellspacing=\"0\" style=\"width:700px;\">\n" +
            "    <tbody>\n" +
            "    <tr>\n" +
            "        <td>\n" +
            "            <div style=\"width:700px;margin:0 auto;border-bottom:1px solid #ccc;margin-bottom:30px;\">\n" +
            "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"700\" height=\"39\"\n" +
            "                       style=\"font:12px Tahoma, Arial, ??????;\">\n" +
            "                    <tbody>\n" +
            "                    <tr>\n" +
            "                        <td width=\"210\"></td>\n" +
            "                    </tr>\n" +
            "                    </tbody>\n" +
            "                </table>\n" +
            "            </div>\n" +
            "            <div style=\"width:680px;padding:0 10px;margin:0 auto;\">\n" +
            "                <div style=\"line-height:1.5;font-size:14px;margin-bottom:25px;color:#4d4d4d;\">\n" +
            "                    <strong style=\"display:block;margin-bottom:15px;\">??????????????????<span\n" +
            "                            style=\"color:#f60;font-size: 16px;\"></span>?????????</strong>\n" +
            "                    <strong style=\"display:block;margin-bottom:15px;\">\n" +
            "                        ???????????????<span style=\"color: red\">";
    public static final String htmlMiddle = "</span>?????????????????????????????????????????????<span\n" +
            "                            style=\"color:#f60;font-size: 24px\">";
    public static final String htmlSuffix = "</span>?????????????????????\n" +
            "                    </strong>\n" +
            "                </div>\n" +
            "                <div style=\"margin-bottom:30px;\">\n" +
            "                    <small style=\"display:block;margin-bottom:20px;font-size:12px;\">\n" +
            "                        <p style=\"color:#747474;\">\n" +
            "                            ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\n" +
            "                            <br>???????????????????????????????????????????????????????????????)\n" +
            "                        </p>\n" +
            "                    </small>\n" +
            "                </div>\n" +
            "            </div>\n" +
            "            <div style=\"width:700px;margin:0 auto;\">\n" +
            "                <div style=\"padding:10px 10px 0;border-top:1px solid #ccc;color:#747474;margin-bottom:20px;line-height:1.3em;font-size:12px;\">\n" +
            "                    <p>?????????????????????????????????<br>\n" +
            "                        ??????????????????????????????????????????????????????\n" +
            "                    </p>\n" +
            "                    <img style=\"width: 15%;\" src=\"http://49.235.100.240/api/source/images/logoTest.png\" alt=\"\">\n" +
            "                </div>\n" +
            "            </div>\n" +
            "        </td>\n" +
            "    </tr>\n" +
            "    </tbody>\n" +
            "</table>\n" +
            "</body>\n" +
            "\n" +
            "\n" +
            "</html>";

}
