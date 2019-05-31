<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns="http://www.w3.org/1999/xhtml" version="2.0">

   <xsl:output encoding="UTF-8" indent="yes" method="html" standalone="no" omit-xml-declaration="yes" />
   <xsl:output name="html" method="html" indent="yes" omit-xml-declaration="yes" />
   <xsl:param name="testRunPertestSuite" />
   <xsl:param name="year" />
   <xsl:param name="testRunsPerMonth" />
   <xsl:param name="usersPerMonth" />
   <xsl:param name="listNumberOfUsersPerTestInLastYear" />

   <xsl:template match="/">
    <html>
         <head>
            <title>TEAM Engine Statistics Report</title>
            <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.0/jquery.min.js"></script>
            <script src="http://code.highcharts.com/highcharts.js"></script>
         </head>
         <body>
            <div id="pichartContainer" style="width: 100%; height: 500px; margin: 0 auto"></div>
            <br />
            <hr />
            <br />
            <div id="barlinechartContainer" style="width: 80%; height: 500px; margin: 0 auto"></div>
            <br />
            <hr />
            <br />
            <div id="pichartusersPerTestContainer" style="width: 100%; height: 500px; margin: 0 auto"></div>
            <script language="JavaScript">
               $(function () {
               
               <!-- RunsPerTestSuiteInLastYear -->
               $(&apos;#pichartContainer&apos;).highcharts({
               chart: {
               plotBackgroundColor: null,
               plotBorderWidth: null,
               plotShadow: false,
               type: &apos;pie&apos;
               },
               credits: {
               enabled: false
               },
               title: {
               text: &apos;Runs per test suite in <xsl:value-of select="$year" />&apos;
               },
               tooltip: {
               pointFormat: &apos;<xsl:text disable-output-escaping="yes"><![CDATA[{series.name}: <b>{point.y}</b>]]></xsl:text>&apos;
               },
               plotOptions: {
               pie: {
               size:&apos;80%&apos;,
               allowPointSelect: true,
               cursor: &apos;pointer&apos;,
               dataLabels: {
               enabled: true,
               format: &apos;<xsl:text disable-output-escaping="yes"><![CDATA[{point.name}: <b>{point.y}</b>]]></xsl:text>&apos;,
               style: {
               color: <xsl:text disable-output-escaping="yes"><![CDATA[(Highcharts.theme && Highcharts.theme.contrastTextColor)]]> </xsl:text> || &apos;black&apos;
               }
               }
               }
               },
               series: [{
               name: &apos;Count&apos;,
               colorByPoint: true,
               data: <xsl:value-of select="$testRunPertestSuite" />
               }]
               });
               
               <!-- TotalNumberOfTestsAndUsersPerMonth -->
               Highcharts.chart(&apos;barlinechartContainer&apos;, {
                   chart: {
                       zoomType: &apos;xy&apos;
                   },
               	credits: {
               		enabled: false
               		},
                   title: {
                       text: &apos;Total number of tests and users per month in <xsl:value-of select="$year" />&apos;
                   },
                   xAxis: [{
                       categories: [&apos;Jan&apos;, &apos;Feb&apos;, &apos;Mar&apos;, &apos;Apr&apos;, &apos;May&apos;, &apos;Jun&apos;,
                           &apos;Jul&apos;, &apos;Aug&apos;, &apos;Sep&apos;, &apos;Oct&apos;, &apos;Nov&apos;, &apos;Dec&apos;],
                       crosshair: true
                   }],
                   yAxis: [{ // Primary yAxis
                       labels: {
                           format: &apos;{value}&apos;,
                           style: {
                               color: Highcharts.getOptions().colors[1]
                           }
                       },
                       title: {
                           text: &apos;Total tests per month in <xsl:value-of select="$year" />&apos;,
                           style: {
                               color: Highcharts.getOptions().colors[1]
                           }
                       },
               		opposite: true
                   }, { // Secondary yAxis
                       title: {
                           text: &apos;Number of users per month in <xsl:value-of select="$year" />&apos;,
                           style: {
                               color: Highcharts.getOptions().colors[0]
                           }
                       },
                       labels: {
                           format: &apos;{value}&apos;,
                           style: {
                               color: Highcharts.getOptions().colors[0]
                           }
                       }
                   }],
                   tooltip: {
                       shared: true
                   },
                   legend: {
                       layout: &apos;vertical&apos;,
                       align: &apos;right&apos;,
                       //x: 120,
                       verticalAlign: &apos;top&apos;,
                       //y: 100,
                       floating: true,
                       backgroundColor: <xsl:text disable-output-escaping="yes"><![CDATA[(Highcharts.theme && Highcharts.theme.legendBackgroundColor) || 'rgba(255,255,255,0.25)']]> </xsl:text>
                   },
                   series: [{
                       name: &apos;Total tests per month in <xsl:value-of select="$year" />&apos;,
                       type: &apos;column&apos;,
                       yAxis: 1,
                       data: [135,129,88,137,17,0,0,0,0,0,0,0]
                   }, {
                       name: &apos;Number of users per month in <xsl:value-of select="$year" />&apos;,
                       type: &apos;spline&apos;,
                       data: [32,39,30,41,4,0,0,0,0,0,0,0]
                   }]
               });
               
               <!-- NumberOfUsersPerTestSuiteInLastYear -->
               $(&apos;#pichartusersPerTestContainer&apos;).highcharts({
                    chart: {
                        plotBackgroundColor: null,
                        plotBorderWidth: null,
                        plotShadow: false,
                        type: &apos;pie&apos;
                    },
                  credits: {
                  enabled: false
                  },
                    title: {
                        text: &apos;Number of users per test suite in <xsl:value-of select="$year" />&apos;
                    },
                    tooltip: {
                        pointFormat: <xsl:text disable-output-escaping="yes"><![CDATA['{series.name}: <b>{point.y}</b>']]> </xsl:text>
                    },
                    plotOptions: {
                        pie: {
                        size:&apos;80%&apos;,
                            allowPointSelect: true,
                            cursor: &apos;pointer&apos;,
                            dataLabels: {
                                enabled: true,
                                format: <xsl:text disable-output-escaping="yes"><![CDATA['<b>{point.name}</b> : {point.y}']]> </xsl:text>,
                                style: {
                                    color: <xsl:text disable-output-escaping="yes"><![CDATA[(Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black' ]]> </xsl:text>
                                }
                            }
                        }
                    },
                    series: [{
                        name: &apos;Users&apos;,
                        colorByPoint: true,
                        data: <xsl:value-of select="$listNumberOfUsersPerTestInLastYear" />
                    }]
                });
               });
            </script>
         </body>
      </html>

   </xsl:template>
</xsl:stylesheet>