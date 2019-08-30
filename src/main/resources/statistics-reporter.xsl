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
   <xsl:param name="wfs20StatusDrilldownResult" />
   <xsl:param name="wfs20FailedTestDrillDownData" />

   <xsl:template match="/">
    <html>
         <head>
            <title>TEAM Engine Statistics Report</title>
            <script src = "https://ajax.googleapis.com/ajax/libs/jquery/2.2.0/jquery.min.js">  </script>
		    <link rel = "stylesheet" type = "text/css" href = "https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.8.0/Chart.min.css"></link>
		    <script src = "https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.8.0/Chart.js">  </script>
		    <script src = "https://cdn.jsdelivr.net/npm/chartjs-plugin-colorschemes">  </script>
         </head>
         <body>
            <div class = "chart-container">
         <canvas id = "testRunPertestSuite" style = "position:relative; width:80vw; height:80vh">  </canvas>
      </div>
      <br />
      <hr />
      <br/>
      <div class = "chart-container">
         <canvas id = "barLineChartContainer" style = "position:relative; width:80vw; height:80vh">  </canvas>
      </div>
      <br />
      <hr />
      <br/>
      <div class = "chart-container">
         <canvas id = "pichartusersPerTestContainer" style = "position:relative; width:80vw; height:80vh">  </canvas>
      </div>
      <br />
      <hr />
      <br/>
      <div class = "chart-container">
         <canvas id = "userExecutedWfs20RunsPerMonthContainer" style = "position:relative; width:80vw; height:80vh">  </canvas>
      </div>
      <br />
      <hr />
      <br/>
      <div class = "chart-container">
         <canvas id = "wfs20StandardsRunsPerMonth" style = "position:relative; width:80vw; height:80vh">  </canvas>
      </div>
      <br />
      <hr />
      <br/>
      <div class = "chart-container">
         <canvas id = "wfs20StandardSuccessFailureContainer" style = "position:relative; width:80vw; height:80vh">  </canvas>
      </div>
      <br />
      <hr />
      <br />
      <div class="chart-container" >
      <canvas id="drilldown-pie" style="position:relative; width:80vw; height:70vh"></canvas>
      </div>
      <div class="chart-container" style="position:relative;" id="failure-pie-chart-div">
      <canvas id="failure-pie-chart" style="position:relative; width:80vw; height:70vh"></canvas>
      <button type="button" style="position:absolute; top:100px; right:200px;" onclick="toggleChart();">Back </button>
      </div>
      <br />
      <hr />
      <br/>
      <div class = "chart-container">
         <canvas id = "UserExecutedKml22RunsPerMonthContainer" style = "position:relative; width:80vw; height:80vh">  </canvas>
      </div>
      <br />
      <hr />
      <br/>
      <div class = "chart-container">
         <canvas id = "kml22StandardsRunsPerMonth" style = "position:relative; width:80vw; height:80vh">  </canvas>
      </div>
      <br />
      <hr />
      <br/>
      <div class = "chart-container">
         <canvas id = "kml22StandardSuccessFailure" style = "position:relative; width:80vw; height:80vh">  </canvas>
      </div>
            <script language="JavaScript">
               $(function () {
               
            <!-- RunsPerTestSuiteInLastYear -->
            var RunsPerTestSuiteInLastYear_pie_chart = $("#testRunPertestSuite");
            var sorted_pie_data = ArraySort(<xsl:value-of select="$testRunPertestSuite" />, function (a, b) {
                              return a - b
                           });
            var s_label = Object.keys(sorted_pie_data);
            var s_data = Object.values(sorted_pie_data);

            new Chart(RunsPerTestSuiteInLastYear_pie_chart, {
                  type: &apos;pie&apos;,
                  data: {
                     labels: s_label,
                     datasets: [{
                           label: &quot;wfs20&quot;,
                           data: s_data,
                        }
                     ]
                  },
                  options: {
                     responsive: true,
                     title: {
                        display: true,
                        position: &quot;top&quot;,
                        text: &quot;Runs per test suite in <xsl:value-of select="$year" />&quot;,
                        fontSize: 18,
                        fontColor: &quot;#111&quot;
                     },
                     legend: {
                        display: true,
                        position: &quot;bottom&quot;,
                        labels: {
                           boxWidth: 15,
                           fontColor: &quot;#333&quot;,
                           fontSize: 12
                        }
                     }
                  }
               });
               
               <!-- TotalNumberOfTestsAndUsersPerMonth -->
               var barlinechartContainer = $(&quot;#barLineChartContainer&quot;);
		       new Chart(barlinechartContainer, {
      				type: &apos;bar&apos;,
      				data: {
      					datasets: [{
      							label: &apos;Total tests per month in <xsl:value-of select="$year" />&apos;,
      							yAxisID: &apos;A&apos;,
      							data: <xsl:value-of select="$testRunsPerMonth" />
      						}, {
      							label: &apos;Number of users per month in <xsl:value-of select="$year" />&apos;,
      							yAxisID: &apos;B&apos;,
      							data: <xsl:value-of select="$usersPerMonth" />,
      
      							// Changes this dataset to become a line
      							type: &apos;line&apos;,
      							fill: false
      						}
      					],
      					labels: [&apos;Jan&apos;, &apos;Feb&apos;, &apos;Mar&apos;, &apos;Apr&apos;, &apos;May&apos;, &apos;Jun&apos;, &apos;Jul&apos;, &apos;Aug&apos;, &apos;Sep&apos;, &apos;Oct&apos;, &apos;Nov&apos;, &apos;Dec&apos;]
      				},
      				options: {
      					title: {
      					  display: true,
      					  text: &apos;Total number of tests and users per month in <xsl:value-of select="$year" />&apos;
      					},
      					legend: {
      					   display: true,
      					   position: &apos;bottom&apos;,
      				  },
      					scales: {
      						xAxes: [{
      							gridLines: {
      								display:false
      							}
      						}],
      						yAxes: [{
      							id: &apos;A&apos;,
      							ticks: {
      								//stepSize: 5,
      								beginAtZero: true
      							},
      							position: &apos;left&apos;,
      							scaleLabel: {
      								display: true,
      								labelString: &apos;Total tests per month in <xsl:value-of select="$year" />&apos;
      							},
      							gridLines: {
      							   display:false
      							}   
      						}, 	{
      							id: &apos;B&apos;,
      							ticks: {
      								//stepSize: 5,
      								beginAtZero: true
      							},
      							position: &apos;right&apos;,
      							scaleLabel: {
      								display: true,
      								labelString: &apos;Number of users per month in <xsl:value-of select="$year" />&apos;
      							},
      							gridLines: {
      							   display:false
      							} 
      						}]
      					}
      				  }
      			});
               
               <!-- NumberOfUsersPerTestSuiteInLastYear -->
               var pichartUsersPerTestContainer = $(&quot;#pichartusersPerTestContainer&quot;);
               var sorted_pie_data = ArraySort(<xsl:value-of select="$listNumberOfUsersPerTestInLastYear" />, function (a, b) {
					return a - b
				});
      			var s_label = Object.keys(sorted_pie_data);
      			var s_data = Object.values(sorted_pie_data);
      
      			new Chart(pichartUsersPerTestContainer, {
      					type: &apos;pie&apos;,
      					data: {
      						labels: s_label,
      						datasets: [{
      								label: &quot;Users&quot;,
      								data: s_data,
      							}
      						]
      					},
      					options: {
      						responsive: true,
      						title: {
      							display: true,
      							position: &quot;top&quot;,
      							text: &quot;Number of users per test suite in <xsl:value-of select="$year" />&quot;,
      							fontSize: 18,
      							fontColor: &quot;#111&quot;
      						},
      						legend: {
      							display: true,
      							position: &quot;bottom&quot;,
      							labels: {
      								boxWidth: 15,
      								fontColor: &quot;#333&quot;,
      								fontSize: 12
      							}
      						}
      					}
      				});
                
               <!-- ******************************************************************* -->
               <!-- **********               WFS 2.0                    *************** -->
               <!-- ******************************************************************* -->
                
                <!-- UserExecutedWfs20RunsPerMonthContainer -->
                new Chart(document.getElementById(&quot;userExecutedWfs20RunsPerMonthContainer&quot;), {
         			type: &apos;line&apos;,
         			responsive: true,
         			maintainAspectRatio: false,
         			data: {
         				labels: [&apos;Jan&apos;, &apos;Feb&apos;, &apos;Mar&apos;, &apos;Apr&apos;, &apos;May&apos;, &apos;Jun&apos;, &apos;Jul&apos;, &apos;Aug&apos;, &apos;Sep&apos;, &apos;Oct&apos;, &apos;Nov&apos;, &apos;Dec&apos;],
         				datasets: [{
         						data: <xsl:value-of select="$numberOfUsersExecutedwfs20RunsPerMonth" />,
         						label: &quot;WFS 2.0&quot;,
         						borderColor: &quot;#3e95cd&quot;,
         						fill: false
         					}
         				]
         			},
         			options: {
         				title: {
         					display: true,
         					text: &apos;Number of users executed the WFS 2.0 standard per month in <xsl:value-of select="$year" />&apos;,
         					fontSize: 18
         				},
         				legend: {
         					display: true,
         					position: &apos;bottom&apos;,
         				},
         				scales: {
         					xAxes: [{
         							gridLines: {
         								display: false
         							}
         						}
         					],
         					yAxes: [{
         							ticks: {
         								//stepSize: 5,
         								beginAtZero: true
         							},
         							scaleLabel: {
         								display: true,
         								labelString: &apos;Test Count&apos;
         							},
         							gridLines: {
         								// display:false
         							}
         						}
         					]
         				}
         			}
         		});
                
               <!-- wfs20StandardsRunsPerMonth --> 
               new Chart(document.getElementById(&quot;wfs20StandardsRunsPerMonth&quot;), {
         			type: &apos;line&apos;,
         			responsive: true,
         			maintainAspectRatio: false,
         			data: {
         				labels: [&apos;Jan&apos;, &apos;Feb&apos;, &apos;Mar&apos;, &apos;Apr&apos;, &apos;May&apos;, &apos;Jun&apos;, &apos;Jul&apos;, &apos;Aug&apos;, &apos;Sep&apos;, &apos;Oct&apos;, &apos;Nov&apos;, &apos;Dec&apos;],
         				datasets: [{
         						data: <xsl:value-of select="$wfs20RunsPerMonth" />,
         						label: &quot;WFS 2.0&quot;,
         						borderColor: &quot;#3e95cd&quot;,
         						fill: false
         					}
         				]
         			},
         			options: {
         				title: {
         					display: true,
         					text: &apos;WFS 2.0 standard runs per month in <xsl:value-of select="$year" />&apos;,
         					fontSize: 18
         				},
         				legend: {
         					display: true,
         					position: &apos;bottom&apos;,
         				},
         				scales: {
         					xAxes: [{
         							gridLines: {
         								display: false
         							}
         						}
         					],
         					yAxes: [{
         							ticks: {
         								//stepSize: 5,
         								beginAtZero: true
         							},
         							scaleLabel: {
         								display: true,
         								labelString: &apos;Test Count&apos;
         							},
         							gridLines: {
         								// display:false
         							}
         						}
         					]
         				}
         			}
         		});
               
               <!-- wfs20StandardSuccessFailure -->
               new Chart(document.getElementById(&quot;wfs20StandardSuccessFailureContainer&quot;), {
         			type: &apos;bar&apos;,
         			responsive: true,
         			maintainAspectRatio: false,
         			data: {
         				labels: [&apos;Jan&apos;, &apos;Feb&apos;, &apos;Mar&apos;, &apos;Apr&apos;, &apos;May&apos;, &apos;Jun&apos;, &apos;Jul&apos;, &apos;Aug&apos;, &apos;Sep&apos;, &apos;Oct&apos;, &apos;Nov&apos;, &apos;Dec&apos;],
         				datasets: [
         				{
         				  label: &quot;Success&quot;,
         				  backgroundColor: &quot;#33cc33&quot;,
         				  borderWidth: 1,
         				  data: <xsl:value-of select="$successArray" />
         				},
         				{
         				  label: &quot;Failure&quot;,
         				  backgroundColor: &quot;#ff0000&quot;,
         				  borderWidth: 1,
         				  data: <xsl:value-of select="$failureArray" />
         				},
         				{
         				  label: &quot;Incomplete&quot;,
         				  backgroundColor: &quot;#ffff00&quot;,
         				  borderWidth: 1,
         				  data: <xsl:value-of select="$incompleteArray" />
         				}
         			  ]
         			},
         			options: {
         				title: {
         					display: true,
         					text: &apos;WFS 2.0 - Passing, failing and incomplete test runs in <xsl:value-of select="$year" />&apos;,
         					fontSize: 18
         				},
         				legend: {
         					display: true,
         					position: &apos;bottom&apos;,
         				},
         				scales: {
         					xAxes: [{
         							gridLines: {
         								display: false
         							}
         						}
         					],
         					yAxes: [{
         							ticks: {
         								//stepSize: 5,
         								beginAtZero: true
         							},
         							scaleLabel: {
         								display: true,
         								labelString: &apos;Test Count&apos;
         							},
         							gridLines: {
         								// display:false
         							}
         						}
         					]
         				}
         			}
         		});
               
               <!-- ************************* Drilldown Pie Chart ******************************************* -->
               
                  $(&quot;#failure-pie-chart-div&quot;).hide();
                  var drilldown_pie_chart = $(&quot;#drilldown-pie&quot;);
                  var s_label = Object.keys(<xsl:value-of select="$wfs20StatusDrilldownResult" />);
                  var s_data = Object.values(<xsl:value-of select="$wfs20StatusDrilldownResult" />);
                  
                  var drilldownPieChart = new Chart(drilldown_pie_chart, {
                        type: &apos;pie&apos;,
                        data: {
                           labels: s_label,
                           datasets: [{
                                 label: &quot;wfs20&quot;,
                                 data: s_data,
                                 backgroundColor: [&apos;#ffff00&apos;, &apos;#33cc33&apos;, &apos;#ff0000&apos;]
                              }
                           ]
                        },
                        options: {
                           responsive: true,
                           title: {
                              display: true,
                              position: &quot;top&quot;,
                              text: &quot;WFS 2.0 - Passing, failing and incomplete test runs in <xsl:value-of select="$year" />&quot;,
                              fontSize: 18,
                              fontColor: &quot;#111&quot;
                           },
                           legend: {
                              display: true,
                              position: &quot;bottom&quot;,
                              labels: {
                                 boxWidth: 15,
                                 fontColor: &quot;#333&quot;,
                                 fontSize: 16
                              }
                           },
                           &apos;onClick&apos; : function (e, item) {
                                 var activePoints = drilldownPieChart.getElementsAtEvent(e);
                                 var selectedIndex = activePoints[0]._index;
                                 var failureLabel = this.data.labels[selectedIndex];
                                 if(failureLabel == &apos;Failure&apos;){
                                    $(&quot;#drilldown-pie&quot;).hide();
                                    $(&quot;#failure-pie-chart-div&quot;).show();
                                    $(&apos;#failure-pie-chart-div&apos;).focus();
                                 }
                           }
                        }
                     });
               <!-- Failure pie chart graph -->
                  var sorted_failure_pie_data = ArraySort(<xsl:value-of select="$wfs20FailedTestDrillDownData" />, function (a, b) {
                     return a - b
                  });
                 var s_failure_pie_label = Object.keys(sorted_failure_pie_data);
                 var s_failure_pie_data = Object.values(sorted_failure_pie_data);
                 
                 new Chart(document.getElementById(&quot;failure-pie-chart&quot;), {
                     type: &apos;pie&apos;,
                     data: {
                        labels: s_failure_pie_label,
                        datasets: [{
                              label: &quot;Failure tests&quot;,
                              data: s_failure_pie_data
                           }
                        ]
                     },
                     options: {
                        title: {
                           display: true,
                           text: &apos;WFS 2.0 - Passing, failing and incomplete test runs in <xsl:value-of select="$year" />&apos;,
                           fontSize: 16
                        },
                        legend: {
                           display: true,
                           position: &quot;bottom&quot;,
                           labels: {
                              boxWidth: 10,
                              fontColor: &quot;#333&quot;,
                              fontSize: 8
                           }
                        }
                     }
                  });
               
               <!-- ******************************************************************* -->
               <!-- ***************               KML 2.2              **************** -->
               <!-- ******************************************************************* -->
               
               <!-- UserExecutedKml22RunsPerMonthContainer -->
                new Chart(document.getElementById(&quot;UserExecutedKml22RunsPerMonthContainer&quot;), {
         			type: &apos;line&apos;,
         			responsive: true,
         			maintainAspectRatio: false,
         			data: {
         				labels: [&apos;Jan&apos;, &apos;Feb&apos;, &apos;Mar&apos;, &apos;Apr&apos;, &apos;May&apos;, &apos;Jun&apos;, &apos;Jul&apos;, &apos;Aug&apos;, &apos;Sep&apos;, &apos;Oct&apos;, &apos;Nov&apos;, &apos;Dec&apos;],
         				datasets: [{
         						data: <xsl:value-of select="$numberOfUsersExecutedkml22RunsPerMonth" />,
         						label: &quot;KML 2.2&quot;,
         						borderColor: &quot;#3e95cd&quot;,
         						fill: false
         					}
         				]
         			},
         			options: {
         				title: {
         					display: true,
         					text: &apos;Number of users executed the KML 2.2 standard per month in <xsl:value-of select="$year" />&apos;,
         					fontSize: 18
         				},
         				legend: {
         					display: true,
         					position: &apos;bottom&apos;,
         				},
         				scales: {
         					xAxes: [{
         							gridLines: {
         								display: false
         							}
         						}
         					],
         					yAxes: [{
         							ticks: {
         								//stepSize: 5,
         								beginAtZero: true
         							},
         							scaleLabel: {
         								display: true,
         								labelString: &apos;Test Count&apos;
         							},
         							gridLines: {
         								// display:false
         							}
         						}
         					]
         				}
         			}
         		});
                
               <!-- kml22StandardsRunsPerMonth --> 
               new Chart(document.getElementById(&quot;kml22StandardsRunsPerMonth&quot;), {
         			type: &apos;line&apos;,
         			responsive: true,
         			maintainAspectRatio: false,
         			data: {
         				labels: [&apos;Jan&apos;, &apos;Feb&apos;, &apos;Mar&apos;, &apos;Apr&apos;, &apos;May&apos;, &apos;Jun&apos;, &apos;Jul&apos;, &apos;Aug&apos;, &apos;Sep&apos;, &apos;Oct&apos;, &apos;Nov&apos;, &apos;Dec&apos;],
         				datasets: [{
         						data: <xsl:value-of select="$kml22RunsPerMonth" />,
         						label: &quot;KML 2.2&quot;,
         						borderColor: &quot;#3e95cd&quot;,
         						fill: false
         					}
         				]
         			},
         			options: {
         				title: {
         					display: true,
         					text: &apos;KML 2.2 standard runs per month in <xsl:value-of select="$year" />&apos;,
         					fontSize: 18
         				},
         				legend: {
         					display: true,
         					position: &apos;bottom&apos;,
         				},
         				scales: {
         					xAxes: [{
         							gridLines: {
         								display: false
         							}
         						}
         					],
         					yAxes: [{
         							ticks: {
         								//stepSize: 5,
         								beginAtZero: true
         							},
         							scaleLabel: {
         								display: true,
         								labelString: &apos;Test Count&apos;
         							},
         							gridLines: {
         								// display:false
         							}
         						}
         					]
         				}
         			}
         		});
               
               <!-- kml22StandardSuccessFailure -->
               new Chart(document.getElementById(&quot;kml22StandardSuccessFailure&quot;), {
                     type: &apos;bar&apos;,
                     responsive: true,
                     maintainAspectRatio: false,
                     data: {
                        labels: [&apos;Jan&apos;, &apos;Feb&apos;, &apos;Mar&apos;, &apos;Apr&apos;, &apos;May&apos;, &apos;Jun&apos;, &apos;Jul&apos;, &apos;Aug&apos;, &apos;Sep&apos;, &apos;Oct&apos;, &apos;Nov&apos;, &apos;Dec&apos;],
                        datasets: [
                        {
                          label: &quot;Success&quot;,
                          backgroundColor: &quot;#33cc33&quot;,
                          borderWidth: 1,
                          data: <xsl:value-of select="$kml22SuccessArray" />
                        },
                        {
                          label: &quot;Failure&quot;,
                          backgroundColor: &quot;#ff0000&quot;,
                          borderWidth: 1,
                          data: <xsl:value-of select="$kml22FailureArray" />
                        },
                        {
                          label: &quot;Incomplete&quot;,
                          backgroundColor: &quot;#ffff00&quot;,
                          borderWidth: 1,
                          data: <xsl:value-of select="$kml22IncompleteArray" />
                        }
                       ]
                     },
                     options: {
                        title: {
                           display: true,
                           text: &apos;KML 2.2 standard success, failures and incomplete by runs per month in <xsl:value-of select="$year" />&apos;,
                           fontSize: 18
                        },
                        legend: {
                           display: true,
                           position: &apos;bottom&apos;,
                        },
                        scales: {
                           xAxes: [{
                                 gridLines: {
                                    display: false
                                 }
                              }
                           ],
                           yAxes: [{
                                 ticks: {
                                    //stepSize: 5,
                                    beginAtZero: true
                                 },
                                 scaleLabel: {
                                    display: true,
                                    labelString: &apos;Test Count&apos;
                                 },
                                 gridLines: {
                                    // display:false
                                 }
                              }
                           ]
                        }
                     }
                  });
                
            });
            <!-- Toggle drilldown pie chart -->
            function toggleChart(){
              $("#failure-pie-chart-div").hide();
              $("#drilldown-pie").show();
              }
            
            <!-- Function to sort Associative Array by its values. -->
            ArraySort = function (array, sortFunc) {
               var tmp = [];
               var aSorted = [];
               var oSorted = {};
      
               for (var k in array) {
                  if (array.hasOwnProperty(k))
                     tmp.push({
                        key: k,
                        value: array[k]
                     });
               }
      
               tmp.sort(function (o1, o2) {
                  return sortFunc(o1.value, o2.value);
               });
      
               if (Object.prototype.toString.call(array) === '[object Array]') {
                  $.each(tmp, function (index, value) {
                     aSorted.push(value.value);
                  });
                  return aSorted;
               }
      
               if (Object.prototype.toString.call(array) === '[object Object]') {
                  $.each(tmp, function (index, value) {
                     oSorted[value.key] = value.value;
                  });
                  return oSorted;
               }
            };
            </script>
         </body>
      </html>

   </xsl:template>
</xsl:stylesheet>