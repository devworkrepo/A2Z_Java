package com.a2zsuvidhaa.in;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

public class PidParser2 {


    public  static String[] parse(String xml) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xmlPullParser = factory.newPullParser();

        xmlPullParser.setInput(new StringReader(xml));
    /*    xmlPullParser.setInput(new StringReader("<?xml version=\"1.0\"?>\n" +
                "<PidData>\n" +
                "  <Resp errCode=\"0\" errInfo=\"Success\" fCount=\"1\" fType=\"2\" nmPoints=\"30\" qScore=\"72\" />\n" +
                "  <DeviceInfo dpId=\"MANTRA.MSIPL\" rdsId=\"MANTRA.WIN.001\" rdsVer=\"1.0.0\" mi=\"MFS100\" mc=\"MIIEGzCCAwOgAwIBAgIIdkuCckhUIJMwDQYJKoZIhvcNAQELBQAwgekxKjAoBgNVBAMTIURTIE1hbnRyYSBTb2Z0ZWNoIEluZGlhIFB2dCBMdGQgNTFNMEsGA1UEMxNEQiAyMDMgU2hhcGF0aCBIZXhhIG9wcG9zaXRlIEd1amFyYXQgSGlnaCBDb3VydCBTIEcgSGlnaHdheSBBaG1lZGFiYWQxEjAQBgNVBAkTCUFobWVkYWJhZDEQMA4GA1UECBMHR3VqYXJhdDESMBAGA1UECxMJVGVjaG5pY2FsMSUwIwYDVQQKExxNYW50cmEgU29mdGVjaCBJbmRpYSBQdnQgTHRkMQswCQYDVQQGEwJJTjAeFw0xOTA5MTkwNjIyMjJaFw0xOTEwMTkwNjM3MDRaMIGwMSQwIgYJKoZIhvcNAQkBFhVzdXBwb3J0QG1hbnRyYXRlYy5jb20xCzAJBgNVBAYTAklOMRAwDgYDVQQIEwdHVUpBUkFUMRIwEAYDVQQHEwlBSE1FREFCQUQxDjAMBgNVBAoTBU1TSVBMMR4wHAYDVQQLExVCaW9tZXRyaWMgTWFudWZhY3R1cmUxJTAjBgNVBAMTHE1hbnRyYSBTb2Z0ZWNoIEluZGlhIFB2dCBMdGQwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC2m2w5Pl17jMLPoNfC4mqg2xUSYJ9pg/PBzP7FrfKiNNT8lrzCD0jzqcv0CMnilm1FL/LSBOpZGOTr8GeofFNApTlasVOC83/keMUyc+xp1+STXcdlZn1y7x1175T/WcKmXmUxcwTPSaaLtn1kdnX5ZM/P1pMDS7zySLU9mrg0ygNTO/oIWf2OX/tpiqzD3J7VnInXt5vzyk79lxnoKOp75qqmwJ3rPLqPWyxf4lCXlcvnkj/hb4tPIsJ1YfTENdJCCD4C4AmqiqeGhehjMzoT3ZMo+EpOOw/zpw27e5q59DX98dVm04JL9TtkiZocLG+rqIGqjLLZOr95ClbWaPPfAgMBAAEwDQYJKoZIhvcNAQELBQADggEBADp4aanFr/Cx01HwlZTHyVEaYTF2jkv/FkzfIPHFrcem+24Q3cD5iBoBaQFsKZoer3C8YJgyAkgVsc0PQ4MdkcSQRnVYr03sgRIdQVrXHG0en255x4zkKpMY7MykyD8TISRzFVTFxUJrQHzycwp7umwjd0M3SaPH0qcarJs0lX0d46L/JghwbyCnUtMW8ldzxVxE1YkdJjlrvLVMwcFLvchvAIanRMGQD4hfDWvFd398pFUNKp7wLD6njN8Wgb5cueSsk31KcI2viOkoXnlfWMkwQN/+RxnS4ukJeARDE1N4MJIa9WaO7yKMqPn9pBYEnatS7gkiGMSd/c7kNaH3Prc=\" dc=\"e769e1c9-dafd-441d-877b-277b7a05419c\">\n" +
                "    <additional_info>\n" +
                "      <Param name=\"srno\" value=\"0401665\" />\n" +
                "      <Param name=\"sysid\" value=\"6A7FEBF307F6E5EFBFF0\" />\n" +
                "      <Param name=\"ts\" value=\"2019-09-21T11:42:16+05:30\" />\n" +
                "    </additional_info>\n" +
                "  </DeviceInfo>\n" +
                "  <Skey ci=\"20191230\">hzOSFgRJCuCQxg94mIe6Zlk31UbKus3KkgqKnkZKwo3pR/riA8lkmR+hEUhmDtbOrJyxeCEUwjPgO2jWaInyG92fml1K9xxMmdvoikJhlKTgOCFjG2kvEzly6xiYMLO9dX9OnqrZ+rC1vx1w5NMUKoSjTy2jhx/e+vtVvC2jBm+MYJI/61vIVmLn+JAECu//wuy5ctvBn84z6LoZqwBW5rdoEsHhO5T/wqdc/DJBukXyOWHlrFEjcTPnetARonx4ef9g1R2BZ2KjJ/6zoGOCaQgoXeKigHD3beGWBeLQCvVNi1NUbVjSPJNMZg6trdg/0wYBOutX1rJDXYoSzKX23Q==</Skey>\n" +
                "  <Hmac>kPdmlc5aSDFbrcy2eWlNhM3vabjqKuMPqpQlqUgadG7hi3DlFN6w8IdoFJWn0nkP</Hmac>\n" +
                "  <Data type=\"X\">MjAxOS0wOS0yMVQxMTo0MjoxNgKJgrYKDr09F+ojJGa5+oLIGDkP0xrm2vvF2LsZ7gFcNM7XPwL7N3zYrA+CePz/D8gdErQnRbxwvCqVLNEq6z/huiL3kBeMJaqzRTkuQZ9EWB2UeCbC+FzGWlmQ6zh+x3Bt6+kI/YP30Te20KjheHyr7rcrlZtLuLJREjtmYF5wz9kCZwkCXWySBYjx5qM4HAyGOGCswVaXD1kiTKzp8Wb/5CKnU6ehDKRS+eJ02qkYH7VJZdR9dCCvWxrV/IGXnsK+rsPta3RYlRICIOyUBB5uWYJ30lxng2ID2FpN0cGvWlHN+EDfSdnSiZDJP3H8HpemwJKge2egoElvrq5nIsGlTcjJHe8QXn+gt5w+5KsgfUGuFB4bH6hNlH8RAiruOp7HcMRSR+Sg0II0v+FXPLaiUUt56EgOB0V11YZBnKmLcH+vaA0U+Q+Q/IhU4RC90vblO6/AN2q7NTwLwVYcEwT2CpR05Mq+5qGJP+Kasftt1pfcJpq791dDEblfqmyv1VZHijruJ10Hon3JT+gyE6bde1k/oWZ+e7XJAwJBeWWesi7W2fMKFL9/TLFg6B2F/5Aa3aHsgitVJp0q3oHHhIAfYd7euVpy9SGZl406tPM/QxPliy9XJmNx5ZSCkCG9UAyWHSk7HQmuBjqJdQO4poz0EbniVUkkWb/YDlTGDnZtfBY7C1M/BeW+EZc5AlFeplt1NhzNJHLP7elsNIb54/2k7gDkNfgdYI+hL8HFvvVbTf1WOKIjymkJmCCubli/n79Udv0DSkb48TLwVXtRPZlBOhXjsG22M5oiGg7QE4qmLDTPaGKx9vYF5gl5Opogq2O0lHrv9BPOXLg/4645kgcWJvza7aqSf/54Cul/1TUz0n8Gcbh0KwJGP6ZhRxQ0bfg6seSzMAhr4jlczcORPI8XOpviY13z/0Q+PK/pq0LEo6+X0LuwxwOCpi1srGyyiOK01gGh3t6F5aD3PTPxXEAJd8AEtw+NXX3aZ+8wPkDvfBcCoOl9eP5xv7vZ3/Zp0ycy5qX8o+9ys7uTIR4JBUUeFzCs1/Q1eiFIl/Wuz/HomUKTsNZKWoDvM2+wiuQxZ4uR67V/h1fkdMC7TnMZOH43h9XZvw==</Data>\n" +
                "</PidData>"));*/

        String[] respStrings = {"na","na"};

        int eventType = xmlPullParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                System.out.println("Start document");
            } else if (eventType == XmlPullParser.START_TAG) {
                if (xmlPullParser.getName().equalsIgnoreCase("Resp")) {

                    int count = xmlPullParser.getAttributeCount();
                    for (int i = 0; i < count; i++) {
                        String attributeName = xmlPullParser.getAttributeName(i);
                        System.out.println(attributeName);

                        if(attributeName.equalsIgnoreCase("errCode")){
                            respStrings[0] = xmlPullParser.getAttributeValue(i);
                            System.out.println("errCode : " + xmlPullParser.getAttributeValue(i));
                        }
                        if(attributeName.equalsIgnoreCase("errInfo")){
                            respStrings[1] = xmlPullParser.getAttributeValue(i);
                            System.out.println("errInfo : " + xmlPullParser.getAttributeValue(i));
                        }
                    }

                }
            }
            eventType = xmlPullParser.next();
        }
        return respStrings;
    }
}
