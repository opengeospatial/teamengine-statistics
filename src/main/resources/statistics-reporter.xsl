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
   <xsl:param name="numberOfUsersExecutedwfs20RunsPerMonth" />
   <xsl:param name="wfs20RunsPerMonth" />
   <xsl:param name="successArray" />
   <xsl:param name="failureArray" />
   <xsl:param name="incompleteArray" />
   <xsl:param name="numberOfUsersExecutedkml22RunsPerMonth" />
   <xsl:param name="kml22RunsPerMonth" />
   <xsl:param name="kml22SuccessArray" />
   <xsl:param name="kml22FailureArray" />
   <xsl:param name="kml22IncompleteArray" />

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
            <br />
            <hr />
            <br />
            <div id="userExecutedWfs20RunsPerMonthContainer" style="width: 100%; height: 500px; margin: 0 auto"></div>
            <br />
            <hr />
            <br />
            <div id="wfs20StandardsRunsPerMonth" style="width: 100%; height: 500px; margin: 0 auto"></div>
            <br />
            <hr />
            <br />
            <div id="wfs20StandardSuccessFailureContainer" style="width: 100%; height: 500px; margin: 0 auto"></div>
            <br />
            <hr />
            <br />
            <div id="userExecutedKml22RunsPerMonthContainer" style="width: 100%; height: 500px; margin: 0 auto"></div>
            <br />
            <hr />
            <br />
            <div id="kml22StandardsRunsPerMonth" style="width: 100%; height: 500px; margin: 0 auto"></div>
            <br />
            <hr />
            <br />
            <div id="kml22StandardSuccessFailureContainer" style="width: 100%; height: 500px; margin: 0 auto"></div>
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
                       data: <xsl:value-of select="$testRunsPerMonth" />
                   }, {
                       name: &apos;Number of users per month in <xsl:value-of select="$year" />&apos;,
                       type: &apos;spline&apos;,
                       data: <xsl:value-of select="$usersPerMonth" />
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
                
               <!-- ******************************************************************* -->
               <!-- **********               WFS 2.0                    *************** -->
               <!-- ******************************************************************* -->
                
                <!-- UserExecutedWfs20RunsPerMonthContainer -->
                Highcharts.chart(&apos;userExecutedWfs20RunsPerMonthContainer&apos;, {
            
                   chart: {
                       type: &apos;spline&apos;
                   },
                   credits: {
                     enabled: false
                   },
                   title: {
                       text: &apos;Number of users executed the WFS 2.0 standard per month in <xsl:value-of select="$year" />&apos;
                   },
                   xAxis: {
                       categories: [&apos;Jan&apos;, &apos;Feb&apos;, &apos;Mar&apos;, &apos;Apr&apos;, &apos;May&apos;, &apos;Jun&apos;, &apos;Jul&apos;, &apos;Aug&apos;, &apos;Sep&apos;, &apos;Oct&apos;, &apos;Nov&apos;, &apos;Dec&apos;]
                   },
                   yAxis: {
                       title: {
                           text: &apos;Test count&apos;
                       }
                   },
                   series: [{
                       name: &apos;WFS 2.0&apos;,
                       data: <xsl:value-of select="$numberOfUsersExecutedwfs20RunsPerMonth" />
                   }]
               
               });
                
               <!-- wfs20StandardsRunsPerMonth --> 
               Highcharts.chart(&apos;wfs20StandardsRunsPerMonth&apos;, {
               
                   chart: {
                       type: &apos;spline&apos;
                   },
                   title: {
                       text: &apos;WFS 2.0 standard runs per month in <xsl:value-of select="$year" />&apos;
                   },
                   xAxis: {
                       categories: [&apos;Jan&apos;, &apos;Feb&apos;, &apos;Mar&apos;, &apos;Apr&apos;, &apos;May&apos;, &apos;Jun&apos;, &apos;Jul&apos;, &apos;Aug&apos;, &apos;Sep&apos;, &apos;Oct&apos;, &apos;Nov&apos;, &apos;Dec&apos;]
                   },
                   yAxis: {
                       title: {
                           text: &apos;Test count&apos;
                       }
                   },
                   series: [{
                       name: &apos;WFS 2.0&apos;,
                       data: <xsl:value-of select="$wfs20RunsPerMonth" />
                   }]
               }); 
               
               <!-- wfs20StandardSuccessFailure -->
               Highcharts.chart(&apos;wfs20StandardSuccessFailureContainer&apos;, {
                   chart: {
                       type: &apos;column&apos;
                   },
                   title: {
                       text: &apos;WFS 2.0 standard success, failures and incomplete by runs per month in <xsl:value-of select="$year" />&apos;
                   },
                   xAxis: {
                       categories: [&apos;Jan&apos;, &apos;Feb&apos;, &apos;Mar&apos;, &apos;Apr&apos;, &apos;May&apos;, &apos;Jun&apos;, &apos;Jul&apos;, &apos;Aug&apos;, &apos;Sep&apos;, &apos;Oct&apos;, &apos;Nov&apos;, &apos;Dec&apos;],
                       crosshair: true
                   },
                   yAxis: {
                       min: 0,
                       title: {
                           text: &apos;Test Run Count&apos;
                       }
                   },
                   tooltip: {
                       headerFormat: <xsl:text disable-output-escaping="yes"><![CDATA['<span style="font-size:10px">{point.key}</span><table>']]> </xsl:text>,
                       pointFormat: <xsl:text disable-output-escaping="yes"><![CDATA['<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                                    '<td style="padding:0"><b>{point.y}</b></td></tr>']]> </xsl:text>,
                       footerFormat: <xsl:text disable-output-escaping="yes"><![CDATA['</table>']]> </xsl:text>,
                       shared: true,
                       useHTML: true
                   },
                   plotOptions: {
                       column: {
                           pointPadding: 0.2,
                           borderWidth: 0
                       }
                   },
                   series: [{
                       name: &apos;Success&apos;,
                       data: <xsl:value-of select="$successArray" />
               
                   }, {
                       name: &apos;Failure&apos;,
                       data: <xsl:value-of select="$failureArray" />
               
                   }, {
                       name: &apos;Incomplete&apos;,
                       data: <xsl:value-of select="$incompleteArray" />
               
                   }]
               });
               
               <!-- ******************************************************************* -->
               <!-- ***************               KML 2.2              **************** -->
               <!-- ******************************************************************* -->
               
               <!-- UserExecutedKml22RunsPerMonthContainer -->
                Highcharts.chart(&apos;userExecutedKml22RunsPerMonthContainer&apos;, {
            
                   chart: {
                       type: &apos;spline&apos;
                   },
                   credits: {
                     enabled: false
                   },
                   title: {
                       text: &apos;Number of users executed the KML 2.2 standard per month in <xsl:value-of select="$year" />&apos;
                   },
                   xAxis: {
                       categories: [&apos;Jan&apos;, &apos;Feb&apos;, &apos;Mar&apos;, &apos;Apr&apos;, &apos;May&apos;, &apos;Jun&apos;, &apos;Jul&apos;, &apos;Aug&apos;, &apos;Sep&apos;, &apos;Oct&apos;, &apos;Nov&apos;, &apos;Dec&apos;]
                   },
                   yAxis: {
                       title: {
                           text: &apos;Test count&apos;
                       }
                   },
                   series: [{
                       name: &apos;KML 2.2&apos;,
                       data: <xsl:value-of select="$numberOfUsersExecutedkml22RunsPerMonth" />
                   }]
               
               });
                
               <!-- kml22StandardsRunsPerMonth --> 
               Highcharts.chart(&apos;kml22StandardsRunsPerMonth&apos;, {
               
                   chart: {
                       type: &apos;spline&apos;
                   },
                   title: {
                       text: &apos;KML 2.2 standard runs per month in <xsl:value-of select="$year" />&apos;
                   },
                   xAxis: {
                       categories: [&apos;Jan&apos;, &apos;Feb&apos;, &apos;Mar&apos;, &apos;Apr&apos;, &apos;May&apos;, &apos;Jun&apos;, &apos;Jul&apos;, &apos;Aug&apos;, &apos;Sep&apos;, &apos;Oct&apos;, &apos;Nov&apos;, &apos;Dec&apos;]
                   },
                   yAxis: {
                       title: {
                           text: &apos;Test count&apos;
                       }
                   },
                   series: [{
                       name: &apos;KML 2.2&apos;,
                       data: <xsl:value-of select="$kml22RunsPerMonth" />
                   }]
               }); 
               
               <!-- kml22StandardSuccessFailure -->
               Highcharts.chart(&apos;kml22StandardSuccessFailureContainer&apos;, {
                   chart: {
                       type: &apos;column&apos;
                   },
                   title: {
                       text: &apos;KML 2.2 standard success, failures and incomplete by runs per month in <xsl:value-of select="$year" />&apos;
                   },
                   xAxis: {
                       categories: [&apos;Jan&apos;, &apos;Feb&apos;, &apos;Mar&apos;, &apos;Apr&apos;, &apos;May&apos;, &apos;Jun&apos;, &apos;Jul&apos;, &apos;Aug&apos;, &apos;Sep&apos;, &apos;Oct&apos;, &apos;Nov&apos;, &apos;Dec&apos;],
                       crosshair: true
                   },
                   yAxis: {
                       min: 0,
                       title: {
                           text: &apos;Test Run Count&apos;
                       }
                   },
                   tooltip: {
                       headerFormat: <xsl:text disable-output-escaping="yes"><![CDATA['<span style="font-size:10px">{point.key}</span><table>']]> </xsl:text>,
                       pointFormat: <xsl:text disable-output-escaping="yes"><![CDATA['<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                                    '<td style="padding:0"><b>{point.y}</b></td></tr>']]> </xsl:text>,
                       footerFormat: <xsl:text disable-output-escaping="yes"><![CDATA['</table>']]> </xsl:text>,
                       shared: true,
                       useHTML: true
                   },
                   plotOptions: {
                       column: {
                           pointPadding: 0.2,
                           borderWidth: 0
                       }
                   },
                   series: [{
                       name: &apos;Success&apos;,
                       data: <xsl:value-of select="$successArray" />
               
                   }, {
                       name: &apos;Failure&apos;,
                       data: <xsl:value-of select="$failureArray" />
               
                   }, {
                       name: &apos;Incomplete&apos;,
                       data: <xsl:value-of select="$incompleteArray" />
               
                   }]
               });
                
            });
            </script>
         </body>
      </html>

   </xsl:template>
</xsl:stylesheet>