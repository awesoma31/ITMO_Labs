/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
$(document).ready(function() {

    $(".click-title").mouseenter( function(    e){
        e.preventDefault();
        this.style.cursor="pointer";
    });
    $(".click-title").mousedown( function(event){
        event.preventDefault();
    });

    // Ugly code while this script is shared among several pages
    try{
        refreshHitsPerSecond(true);
    } catch(e){}
    try{
        refreshResponseTimeOverTime(true);
    } catch(e){}
    try{
        refreshResponseTimePercentiles();
    } catch(e){}
});


var responseTimePercentilesInfos = {
        data: {"result": {"minY": 508.0, "minX": 0.0, "maxY": 1299.0, "series": [{"data": [[0.0, 508.0], [0.1, 513.0], [0.2, 525.0], [0.3, 539.0], [0.4, 540.0], [0.5, 551.0], [0.6, 555.0], [0.7, 555.0], [0.8, 560.0], [0.9, 563.0], [1.0, 567.0], [1.1, 569.0], [1.2, 570.0], [1.3, 571.0], [1.4, 572.0], [1.5, 573.0], [1.6, 574.0], [1.7, 574.0], [1.8, 574.0], [1.9, 574.0], [2.0, 574.0], [2.1, 575.0], [2.2, 575.0], [2.3, 575.0], [2.4, 575.0], [2.5, 575.0], [2.6, 575.0], [2.7, 575.0], [2.8, 575.0], [2.9, 575.0], [3.0, 575.0], [3.1, 575.0], [3.2, 575.0], [3.3, 575.0], [3.4, 575.0], [3.5, 575.0], [3.6, 575.0], [3.7, 575.0], [3.8, 575.0], [3.9, 575.0], [4.0, 575.0], [4.1, 575.0], [4.2, 575.0], [4.3, 576.0], [4.4, 576.0], [4.5, 576.0], [4.6, 576.0], [4.7, 576.0], [4.8, 576.0], [4.9, 576.0], [5.0, 576.0], [5.1, 576.0], [5.2, 576.0], [5.3, 576.0], [5.4, 576.0], [5.5, 576.0], [5.6, 576.0], [5.7, 576.0], [5.8, 576.0], [5.9, 576.0], [6.0, 576.0], [6.1, 576.0], [6.2, 576.0], [6.3, 576.0], [6.4, 576.0], [6.5, 576.0], [6.6, 576.0], [6.7, 576.0], [6.8, 576.0], [6.9, 576.0], [7.0, 576.0], [7.1, 576.0], [7.2, 576.0], [7.3, 576.0], [7.4, 576.0], [7.5, 576.0], [7.6, 576.0], [7.7, 576.0], [7.8, 576.0], [7.9, 576.0], [8.0, 576.0], [8.1, 576.0], [8.2, 576.0], [8.3, 576.0], [8.4, 576.0], [8.5, 576.0], [8.6, 576.0], [8.7, 576.0], [8.8, 576.0], [8.9, 576.0], [9.0, 576.0], [9.1, 576.0], [9.2, 576.0], [9.3, 576.0], [9.4, 576.0], [9.5, 576.0], [9.6, 576.0], [9.7, 576.0], [9.8, 576.0], [9.9, 576.0], [10.0, 576.0], [10.1, 576.0], [10.2, 576.0], [10.3, 576.0], [10.4, 576.0], [10.5, 576.0], [10.6, 576.0], [10.7, 576.0], [10.8, 576.0], [10.9, 576.0], [11.0, 576.0], [11.1, 576.0], [11.2, 576.0], [11.3, 576.0], [11.4, 576.0], [11.5, 576.0], [11.6, 576.0], [11.7, 576.0], [11.8, 576.0], [11.9, 576.0], [12.0, 576.0], [12.1, 576.0], [12.2, 576.0], [12.3, 576.0], [12.4, 576.0], [12.5, 576.0], [12.6, 576.0], [12.7, 576.0], [12.8, 576.0], [12.9, 576.0], [13.0, 576.0], [13.1, 577.0], [13.2, 577.0], [13.3, 577.0], [13.4, 577.0], [13.5, 577.0], [13.6, 577.0], [13.7, 577.0], [13.8, 577.0], [13.9, 577.0], [14.0, 577.0], [14.1, 577.0], [14.2, 577.0], [14.3, 577.0], [14.4, 577.0], [14.5, 577.0], [14.6, 577.0], [14.7, 577.0], [14.8, 577.0], [14.9, 577.0], [15.0, 577.0], [15.1, 577.0], [15.2, 577.0], [15.3, 577.0], [15.4, 577.0], [15.5, 577.0], [15.6, 577.0], [15.7, 577.0], [15.8, 577.0], [15.9, 577.0], [16.0, 577.0], [16.1, 577.0], [16.2, 577.0], [16.3, 577.0], [16.4, 577.0], [16.5, 577.0], [16.6, 577.0], [16.7, 577.0], [16.8, 577.0], [16.9, 577.0], [17.0, 577.0], [17.1, 577.0], [17.2, 577.0], [17.3, 577.0], [17.4, 577.0], [17.5, 577.0], [17.6, 577.0], [17.7, 577.0], [17.8, 577.0], [17.9, 577.0], [18.0, 577.0], [18.1, 577.0], [18.2, 577.0], [18.3, 577.0], [18.4, 577.0], [18.5, 577.0], [18.6, 577.0], [18.7, 577.0], [18.8, 577.0], [18.9, 577.0], [19.0, 577.0], [19.1, 577.0], [19.2, 577.0], [19.3, 577.0], [19.4, 577.0], [19.5, 577.0], [19.6, 577.0], [19.7, 577.0], [19.8, 577.0], [19.9, 577.0], [20.0, 577.0], [20.1, 577.0], [20.2, 577.0], [20.3, 577.0], [20.4, 577.0], [20.5, 577.0], [20.6, 577.0], [20.7, 577.0], [20.8, 577.0], [20.9, 577.0], [21.0, 577.0], [21.1, 577.0], [21.2, 577.0], [21.3, 577.0], [21.4, 577.0], [21.5, 577.0], [21.6, 577.0], [21.7, 577.0], [21.8, 577.0], [21.9, 577.0], [22.0, 577.0], [22.1, 577.0], [22.2, 577.0], [22.3, 577.0], [22.4, 577.0], [22.5, 577.0], [22.6, 577.0], [22.7, 577.0], [22.8, 577.0], [22.9, 577.0], [23.0, 577.0], [23.1, 577.0], [23.2, 577.0], [23.3, 577.0], [23.4, 577.0], [23.5, 577.0], [23.6, 577.0], [23.7, 577.0], [23.8, 577.0], [23.9, 577.0], [24.0, 577.0], [24.1, 577.0], [24.2, 577.0], [24.3, 577.0], [24.4, 577.0], [24.5, 577.0], [24.6, 577.0], [24.7, 577.0], [24.8, 577.0], [24.9, 577.0], [25.0, 577.0], [25.1, 577.0], [25.2, 577.0], [25.3, 577.0], [25.4, 577.0], [25.5, 577.0], [25.6, 577.0], [25.7, 577.0], [25.8, 577.0], [25.9, 577.0], [26.0, 577.0], [26.1, 577.0], [26.2, 577.0], [26.3, 577.0], [26.4, 577.0], [26.5, 577.0], [26.6, 577.0], [26.7, 577.0], [26.8, 577.0], [26.9, 577.0], [27.0, 577.0], [27.1, 577.0], [27.2, 577.0], [27.3, 577.0], [27.4, 577.0], [27.5, 577.0], [27.6, 577.0], [27.7, 577.0], [27.8, 577.0], [27.9, 577.0], [28.0, 577.0], [28.1, 577.0], [28.2, 577.0], [28.3, 577.0], [28.4, 577.0], [28.5, 577.0], [28.6, 577.0], [28.7, 577.0], [28.8, 577.0], [28.9, 577.0], [29.0, 577.0], [29.1, 577.0], [29.2, 577.0], [29.3, 577.0], [29.4, 577.0], [29.5, 577.0], [29.6, 577.0], [29.7, 577.0], [29.8, 577.0], [29.9, 577.0], [30.0, 577.0], [30.1, 578.0], [30.2, 578.0], [30.3, 578.0], [30.4, 578.0], [30.5, 578.0], [30.6, 578.0], [30.7, 578.0], [30.8, 578.0], [30.9, 578.0], [31.0, 578.0], [31.1, 578.0], [31.2, 578.0], [31.3, 578.0], [31.4, 578.0], [31.5, 578.0], [31.6, 578.0], [31.7, 578.0], [31.8, 578.0], [31.9, 578.0], [32.0, 578.0], [32.1, 578.0], [32.2, 578.0], [32.3, 578.0], [32.4, 578.0], [32.5, 578.0], [32.6, 578.0], [32.7, 578.0], [32.8, 578.0], [32.9, 578.0], [33.0, 578.0], [33.1, 578.0], [33.2, 578.0], [33.3, 578.0], [33.4, 578.0], [33.5, 578.0], [33.6, 578.0], [33.7, 578.0], [33.8, 578.0], [33.9, 578.0], [34.0, 578.0], [34.1, 578.0], [34.2, 578.0], [34.3, 578.0], [34.4, 578.0], [34.5, 578.0], [34.6, 578.0], [34.7, 578.0], [34.8, 578.0], [34.9, 578.0], [35.0, 578.0], [35.1, 578.0], [35.2, 578.0], [35.3, 578.0], [35.4, 578.0], [35.5, 578.0], [35.6, 578.0], [35.7, 578.0], [35.8, 578.0], [35.9, 578.0], [36.0, 578.0], [36.1, 578.0], [36.2, 578.0], [36.3, 578.0], [36.4, 578.0], [36.5, 578.0], [36.6, 578.0], [36.7, 578.0], [36.8, 578.0], [36.9, 578.0], [37.0, 578.0], [37.1, 578.0], [37.2, 578.0], [37.3, 578.0], [37.4, 578.0], [37.5, 578.0], [37.6, 578.0], [37.7, 578.0], [37.8, 578.0], [37.9, 578.0], [38.0, 578.0], [38.1, 578.0], [38.2, 578.0], [38.3, 578.0], [38.4, 578.0], [38.5, 578.0], [38.6, 578.0], [38.7, 578.0], [38.8, 578.0], [38.9, 578.0], [39.0, 578.0], [39.1, 578.0], [39.2, 578.0], [39.3, 578.0], [39.4, 578.0], [39.5, 578.0], [39.6, 578.0], [39.7, 578.0], [39.8, 578.0], [39.9, 578.0], [40.0, 578.0], [40.1, 578.0], [40.2, 578.0], [40.3, 578.0], [40.4, 578.0], [40.5, 578.0], [40.6, 578.0], [40.7, 578.0], [40.8, 578.0], [40.9, 578.0], [41.0, 578.0], [41.1, 578.0], [41.2, 578.0], [41.3, 578.0], [41.4, 578.0], [41.5, 578.0], [41.6, 578.0], [41.7, 578.0], [41.8, 578.0], [41.9, 578.0], [42.0, 578.0], [42.1, 578.0], [42.2, 578.0], [42.3, 578.0], [42.4, 578.0], [42.5, 578.0], [42.6, 578.0], [42.7, 578.0], [42.8, 578.0], [42.9, 578.0], [43.0, 578.0], [43.1, 578.0], [43.2, 578.0], [43.3, 578.0], [43.4, 578.0], [43.5, 578.0], [43.6, 578.0], [43.7, 578.0], [43.8, 578.0], [43.9, 578.0], [44.0, 578.0], [44.1, 578.0], [44.2, 578.0], [44.3, 578.0], [44.4, 578.0], [44.5, 578.0], [44.6, 578.0], [44.7, 578.0], [44.8, 578.0], [44.9, 578.0], [45.0, 578.0], [45.1, 578.0], [45.2, 578.0], [45.3, 578.0], [45.4, 578.0], [45.5, 578.0], [45.6, 578.0], [45.7, 578.0], [45.8, 578.0], [45.9, 578.0], [46.0, 578.0], [46.1, 578.0], [46.2, 578.0], [46.3, 579.0], [46.4, 579.0], [46.5, 579.0], [46.6, 579.0], [46.7, 579.0], [46.8, 579.0], [46.9, 579.0], [47.0, 579.0], [47.1, 579.0], [47.2, 579.0], [47.3, 579.0], [47.4, 579.0], [47.5, 579.0], [47.6, 579.0], [47.7, 579.0], [47.8, 579.0], [47.9, 579.0], [48.0, 579.0], [48.1, 579.0], [48.2, 579.0], [48.3, 579.0], [48.4, 579.0], [48.5, 579.0], [48.6, 579.0], [48.7, 579.0], [48.8, 579.0], [48.9, 579.0], [49.0, 579.0], [49.1, 579.0], [49.2, 579.0], [49.3, 579.0], [49.4, 579.0], [49.5, 579.0], [49.6, 579.0], [49.7, 579.0], [49.8, 579.0], [49.9, 579.0], [50.0, 579.0], [50.1, 579.0], [50.2, 579.0], [50.3, 579.0], [50.4, 579.0], [50.5, 579.0], [50.6, 579.0], [50.7, 579.0], [50.8, 579.0], [50.9, 579.0], [51.0, 579.0], [51.1, 579.0], [51.2, 579.0], [51.3, 579.0], [51.4, 579.0], [51.5, 579.0], [51.6, 579.0], [51.7, 579.0], [51.8, 579.0], [51.9, 579.0], [52.0, 579.0], [52.1, 579.0], [52.2, 579.0], [52.3, 579.0], [52.4, 579.0], [52.5, 579.0], [52.6, 579.0], [52.7, 579.0], [52.8, 579.0], [52.9, 579.0], [53.0, 579.0], [53.1, 579.0], [53.2, 579.0], [53.3, 579.0], [53.4, 579.0], [53.5, 579.0], [53.6, 579.0], [53.7, 579.0], [53.8, 579.0], [53.9, 579.0], [54.0, 579.0], [54.1, 579.0], [54.2, 579.0], [54.3, 579.0], [54.4, 579.0], [54.5, 579.0], [54.6, 579.0], [54.7, 579.0], [54.8, 579.0], [54.9, 579.0], [55.0, 579.0], [55.1, 579.0], [55.2, 579.0], [55.3, 579.0], [55.4, 579.0], [55.5, 579.0], [55.6, 579.0], [55.7, 579.0], [55.8, 579.0], [55.9, 579.0], [56.0, 579.0], [56.1, 579.0], [56.2, 579.0], [56.3, 579.0], [56.4, 579.0], [56.5, 579.0], [56.6, 579.0], [56.7, 579.0], [56.8, 579.0], [56.9, 579.0], [57.0, 579.0], [57.1, 579.0], [57.2, 579.0], [57.3, 579.0], [57.4, 579.0], [57.5, 579.0], [57.6, 579.0], [57.7, 579.0], [57.8, 579.0], [57.9, 579.0], [58.0, 579.0], [58.1, 579.0], [58.2, 579.0], [58.3, 579.0], [58.4, 579.0], [58.5, 579.0], [58.6, 579.0], [58.7, 579.0], [58.8, 580.0], [58.9, 580.0], [59.0, 580.0], [59.1, 580.0], [59.2, 580.0], [59.3, 580.0], [59.4, 580.0], [59.5, 580.0], [59.6, 580.0], [59.7, 580.0], [59.8, 580.0], [59.9, 580.0], [60.0, 580.0], [60.1, 580.0], [60.2, 580.0], [60.3, 580.0], [60.4, 580.0], [60.5, 580.0], [60.6, 580.0], [60.7, 580.0], [60.8, 580.0], [60.9, 580.0], [61.0, 580.0], [61.1, 580.0], [61.2, 580.0], [61.3, 580.0], [61.4, 580.0], [61.5, 580.0], [61.6, 580.0], [61.7, 580.0], [61.8, 580.0], [61.9, 580.0], [62.0, 580.0], [62.1, 580.0], [62.2, 580.0], [62.3, 580.0], [62.4, 580.0], [62.5, 580.0], [62.6, 580.0], [62.7, 580.0], [62.8, 580.0], [62.9, 580.0], [63.0, 580.0], [63.1, 580.0], [63.2, 580.0], [63.3, 580.0], [63.4, 580.0], [63.5, 580.0], [63.6, 580.0], [63.7, 580.0], [63.8, 580.0], [63.9, 580.0], [64.0, 580.0], [64.1, 580.0], [64.2, 580.0], [64.3, 580.0], [64.4, 580.0], [64.5, 580.0], [64.6, 580.0], [64.7, 580.0], [64.8, 580.0], [64.9, 580.0], [65.0, 580.0], [65.1, 580.0], [65.2, 580.0], [65.3, 580.0], [65.4, 580.0], [65.5, 580.0], [65.6, 580.0], [65.7, 580.0], [65.8, 581.0], [65.9, 581.0], [66.0, 581.0], [66.1, 581.0], [66.2, 581.0], [66.3, 581.0], [66.4, 581.0], [66.5, 581.0], [66.6, 581.0], [66.7, 581.0], [66.8, 581.0], [66.9, 581.0], [67.0, 581.0], [67.1, 581.0], [67.2, 581.0], [67.3, 581.0], [67.4, 581.0], [67.5, 581.0], [67.6, 581.0], [67.7, 581.0], [67.8, 581.0], [67.9, 581.0], [68.0, 581.0], [68.1, 581.0], [68.2, 581.0], [68.3, 581.0], [68.4, 581.0], [68.5, 581.0], [68.6, 581.0], [68.7, 581.0], [68.8, 581.0], [68.9, 581.0], [69.0, 581.0], [69.1, 581.0], [69.2, 581.0], [69.3, 581.0], [69.4, 582.0], [69.5, 582.0], [69.6, 582.0], [69.7, 582.0], [69.8, 582.0], [69.9, 582.0], [70.0, 582.0], [70.1, 582.0], [70.2, 582.0], [70.3, 582.0], [70.4, 582.0], [70.5, 582.0], [70.6, 582.0], [70.7, 582.0], [70.8, 582.0], [70.9, 582.0], [71.0, 582.0], [71.1, 582.0], [71.2, 582.0], [71.3, 582.0], [71.4, 582.0], [71.5, 582.0], [71.6, 582.0], [71.7, 582.0], [71.8, 582.0], [71.9, 582.0], [72.0, 583.0], [72.1, 583.0], [72.2, 583.0], [72.3, 583.0], [72.4, 583.0], [72.5, 583.0], [72.6, 583.0], [72.7, 583.0], [72.8, 583.0], [72.9, 583.0], [73.0, 583.0], [73.1, 583.0], [73.2, 583.0], [73.3, 583.0], [73.4, 583.0], [73.5, 583.0], [73.6, 583.0], [73.7, 583.0], [73.8, 583.0], [73.9, 583.0], [74.0, 583.0], [74.1, 583.0], [74.2, 583.0], [74.3, 583.0], [74.4, 583.0], [74.5, 583.0], [74.6, 583.0], [74.7, 583.0], [74.8, 583.0], [74.9, 583.0], [75.0, 583.0], [75.1, 583.0], [75.2, 583.0], [75.3, 583.0], [75.4, 583.0], [75.5, 583.0], [75.6, 583.0], [75.7, 583.0], [75.8, 583.0], [75.9, 583.0], [76.0, 583.0], [76.1, 583.0], [76.2, 583.0], [76.3, 583.0], [76.4, 583.0], [76.5, 583.0], [76.6, 583.0], [76.7, 583.0], [76.8, 583.0], [76.9, 583.0], [77.0, 583.0], [77.1, 583.0], [77.2, 583.0], [77.3, 583.0], [77.4, 583.0], [77.5, 583.0], [77.6, 583.0], [77.7, 583.0], [77.8, 583.0], [77.9, 583.0], [78.0, 583.0], [78.1, 583.0], [78.2, 583.0], [78.3, 584.0], [78.4, 584.0], [78.5, 584.0], [78.6, 584.0], [78.7, 584.0], [78.8, 584.0], [78.9, 584.0], [79.0, 584.0], [79.1, 584.0], [79.2, 584.0], [79.3, 584.0], [79.4, 584.0], [79.5, 584.0], [79.6, 584.0], [79.7, 584.0], [79.8, 584.0], [79.9, 584.0], [80.0, 584.0], [80.1, 584.0], [80.2, 584.0], [80.3, 584.0], [80.4, 584.0], [80.5, 584.0], [80.6, 584.0], [80.7, 584.0], [80.8, 584.0], [80.9, 584.0], [81.0, 584.0], [81.1, 584.0], [81.2, 584.0], [81.3, 584.0], [81.4, 584.0], [81.5, 584.0], [81.6, 584.0], [81.7, 584.0], [81.8, 584.0], [81.9, 584.0], [82.0, 584.0], [82.1, 584.0], [82.2, 584.0], [82.3, 584.0], [82.4, 584.0], [82.5, 584.0], [82.6, 584.0], [82.7, 584.0], [82.8, 584.0], [82.9, 584.0], [83.0, 584.0], [83.1, 584.0], [83.2, 584.0], [83.3, 584.0], [83.4, 584.0], [83.5, 584.0], [83.6, 584.0], [83.7, 584.0], [83.8, 584.0], [83.9, 584.0], [84.0, 584.0], [84.1, 584.0], [84.2, 584.0], [84.3, 584.0], [84.4, 584.0], [84.5, 584.0], [84.6, 584.0], [84.7, 585.0], [84.8, 585.0], [84.9, 585.0], [85.0, 585.0], [85.1, 585.0], [85.2, 585.0], [85.3, 585.0], [85.4, 585.0], [85.5, 585.0], [85.6, 585.0], [85.7, 585.0], [85.8, 585.0], [85.9, 585.0], [86.0, 585.0], [86.1, 585.0], [86.2, 585.0], [86.3, 585.0], [86.4, 585.0], [86.5, 585.0], [86.6, 585.0], [86.7, 585.0], [86.8, 585.0], [86.9, 585.0], [87.0, 585.0], [87.1, 585.0], [87.2, 585.0], [87.3, 585.0], [87.4, 585.0], [87.5, 585.0], [87.6, 585.0], [87.7, 585.0], [87.8, 586.0], [87.9, 586.0], [88.0, 586.0], [88.1, 586.0], [88.2, 586.0], [88.3, 586.0], [88.4, 586.0], [88.5, 586.0], [88.6, 586.0], [88.7, 586.0], [88.8, 586.0], [88.9, 586.0], [89.0, 586.0], [89.1, 586.0], [89.2, 586.0], [89.3, 587.0], [89.4, 587.0], [89.5, 587.0], [89.6, 587.0], [89.7, 587.0], [89.8, 588.0], [89.9, 588.0], [90.0, 588.0], [90.1, 588.0], [90.2, 588.0], [90.3, 589.0], [90.4, 590.0], [90.5, 590.0], [90.6, 590.0], [90.7, 591.0], [90.8, 592.0], [90.9, 593.0], [91.0, 593.0], [91.1, 594.0], [91.2, 594.0], [91.3, 595.0], [91.4, 595.0], [91.5, 595.0], [91.6, 596.0], [91.7, 596.0], [91.8, 597.0], [91.9, 598.0], [92.0, 599.0], [92.1, 599.0], [92.2, 600.0], [92.3, 601.0], [92.4, 602.0], [92.5, 603.0], [92.6, 603.0], [92.7, 605.0], [92.8, 606.0], [92.9, 606.0], [93.0, 607.0], [93.1, 607.0], [93.2, 608.0], [93.3, 608.0], [93.4, 608.0], [93.5, 609.0], [93.6, 610.0], [93.7, 611.0], [93.8, 612.0], [93.9, 613.0], [94.0, 614.0], [94.1, 614.0], [94.2, 614.0], [94.3, 615.0], [94.4, 616.0], [94.5, 617.0], [94.6, 618.0], [94.7, 619.0], [94.8, 620.0], [94.9, 622.0], [95.0, 622.0], [95.1, 625.0], [95.2, 627.0], [95.3, 628.0], [95.4, 629.0], [95.5, 631.0], [95.6, 632.0], [95.7, 633.0], [95.8, 634.0], [95.9, 636.0], [96.0, 640.0], [96.1, 641.0], [96.2, 642.0], [96.3, 644.0], [96.4, 646.0], [96.5, 647.0], [96.6, 648.0], [96.7, 649.0], [96.8, 651.0], [96.9, 652.0], [97.0, 654.0], [97.1, 655.0], [97.2, 657.0], [97.3, 658.0], [97.4, 659.0], [97.5, 660.0], [97.6, 662.0], [97.7, 664.0], [97.8, 666.0], [97.9, 667.0], [98.0, 670.0], [98.1, 671.0], [98.2, 672.0], [98.3, 673.0], [98.4, 673.0], [98.5, 673.0], [98.6, 674.0], [98.7, 674.0], [98.8, 674.0], [98.9, 675.0], [99.0, 675.0], [99.1, 677.0], [99.2, 690.0], [99.3, 717.0], [99.4, 728.0], [99.5, 766.0], [99.6, 783.0], [99.7, 785.0], [99.8, 841.0], [99.9, 1182.0], [100.0, 1299.0]], "isOverall": false, "label": "HTTP Request", "isController": false}], "supportsControllersDiscrimination": true, "maxX": 100.0, "title": "Response Time Percentiles"}},
        getOptions: function() {
            return {
                series: {
                    points: { show: false }
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimePercentiles'
                },
                xaxis: {
                    tickDecimals: 1,
                    axisLabel: "Percentiles",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Percentile value in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : %x.2 percentile was %y ms"
                },
                selection: { mode: "xy" },
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesResponseTimePercentiles"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimesPercentiles"), dataset, options);
            // setup overview
            $.plot($("#overviewResponseTimesPercentiles"), dataset, prepareOverviewOptions(options));
        }
};

/**
 * @param elementId Id of element where we display message
 */
function setEmptyGraph(elementId) {
    $(function() {
        $(elementId).text("No graph series with filter="+seriesFilter);
    });
}

// Response times percentiles
function refreshResponseTimePercentiles() {
    var infos = responseTimePercentilesInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyResponseTimePercentiles");
        return;
    }
    if (isGraph($("#flotResponseTimesPercentiles"))){
        infos.createGraph();
    } else {
        var choiceContainer = $("#choicesResponseTimePercentiles");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimesPercentiles", "#overviewResponseTimesPercentiles");
        $('#bodyResponseTimePercentiles .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
}

var responseTimeDistributionInfos = {
        data: {"result": {"minY": 1.0, "minX": 500.0, "maxY": 4359.0, "series": [{"data": [[1100.0, 1.0], [600.0, 337.0], [1200.0, 4.0], [700.0, 25.0], [800.0, 3.0], [900.0, 3.0], [500.0, 4359.0]], "isOverall": false, "label": "HTTP Request", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 100, "maxX": 1200.0, "title": "Response Time Distribution"}},
        getOptions: function() {
            var granularity = this.data.result.granularity;
            return {
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimeDistribution'
                },
                xaxis:{
                    axisLabel: "Response times in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of responses",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                bars : {
                    show: true,
                    barWidth: this.data.result.granularity
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: function(label, xval, yval, flotItem){
                        return yval + " responses for " + label + " were between " + xval + " and " + (xval + granularity) + " ms";
                    }
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimeDistribution"), prepareData(data.result.series, $("#choicesResponseTimeDistribution")), options);
        }

};

// Response time distribution
function refreshResponseTimeDistribution() {
    var infos = responseTimeDistributionInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyResponseTimeDistribution");
        return;
    }
    if (isGraph($("#flotResponseTimeDistribution"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesResponseTimeDistribution");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        $('#footerResponseTimeDistribution .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};


var syntheticResponseTimeDistributionInfos = {
        data: {"result": {"minY": 4732.0, "minX": 1.0, "ticks": [[0, "Requests having \nresponse time <= 500ms"], [1, "Requests having \nresponse time > 500ms and <= 1,500ms"], [2, "Requests having \nresponse time > 1,500ms"], [3, "Requests in error"]], "maxY": 4732.0, "series": [{"data": [], "color": "#9ACD32", "isOverall": false, "label": "Requests having \nresponse time <= 500ms", "isController": false}, {"data": [[1.0, 4732.0]], "color": "yellow", "isOverall": false, "label": "Requests having \nresponse time > 500ms and <= 1,500ms", "isController": false}, {"data": [], "color": "orange", "isOverall": false, "label": "Requests having \nresponse time > 1,500ms", "isController": false}, {"data": [], "color": "#FF6347", "isOverall": false, "label": "Requests in error", "isController": false}], "supportsControllersDiscrimination": false, "maxX": 1.0, "title": "Synthetic Response Times Distribution"}},
        getOptions: function() {
            return {
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendSyntheticResponseTimeDistribution'
                },
                xaxis:{
                    axisLabel: "Response times ranges",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                    tickLength:0,
                    min:-0.5,
                    max:3.5
                },
                yaxis: {
                    axisLabel: "Number of responses",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                bars : {
                    show: true,
                    align: "center",
                    barWidth: 0.25,
                    fill:.75
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: function(label, xval, yval, flotItem){
                        return yval + " " + label;
                    }
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var options = this.getOptions();
            prepareOptions(options, data);
            options.xaxis.ticks = data.result.ticks;
            $.plot($("#flotSyntheticResponseTimeDistribution"), prepareData(data.result.series, $("#choicesSyntheticResponseTimeDistribution")), options);
        }

};

// Response time distribution
function refreshSyntheticResponseTimeDistribution() {
    var infos = syntheticResponseTimeDistributionInfos;
    prepareSeries(infos.data, true);
    if (isGraph($("#flotSyntheticResponseTimeDistribution"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesSyntheticResponseTimeDistribution");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        $('#footerSyntheticResponseTimeDistribution .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var activeThreadsOverTimeInfos = {
        data: {"result": {"minY": 1.0, "minX": 1.77910338E12, "maxY": 12.0, "series": [{"data": [[1.77910356E12, 12.0], [1.77910374E12, 12.0], [1.77910368E12, 12.0], [1.77910338E12, 1.0], [1.77910386E12, 12.0], [1.7791038E12, 12.0], [1.7791035E12, 12.0], [1.77910398E12, 11.86283185840708], [1.77910344E12, 7.8294930875576], [1.77910392E12, 12.0], [1.77910362E12, 12.0]], "isOverall": false, "label": "Thread Group", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77910398E12, "title": "Active Threads Over Time"}},
        getOptions: function() {
            return {
                series: {
                    stack: true,
                    lines: {
                        show: true,
                        fill: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of active threads",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 6,
                    show: true,
                    container: '#legendActiveThreadsOverTime'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                selection: {
                    mode: 'xy'
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : At %x there were %y active threads"
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesActiveThreadsOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotActiveThreadsOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewActiveThreadsOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Active Threads Over Time
function refreshActiveThreadsOverTime(fixTimestamps) {
    var infos = activeThreadsOverTimeInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotActiveThreadsOverTime"))) {
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesActiveThreadsOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotActiveThreadsOverTime", "#overviewActiveThreadsOverTime");
        $('#footerActiveThreadsOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var timeVsThreadsInfos = {
        data: {"result": {"minY": 568.3888888888889, "minX": 1.0, "maxY": 602.6216216216216, "series": [{"data": [[1.0, 574.875], [2.0, 581.6666666666667], [4.0, 568.3888888888889], [8.0, 588.1951219512196], [9.0, 592.5238095238095], [5.0, 596.1707317073171], [10.0, 586.1000000000001], [11.0, 593.2250000000001], [3.0, 577.1111111111112], [6.0, 602.6216216216216], [12.0, 585.1062413951331], [7.0, 585.3181818181818]], "isOverall": false, "label": "HTTP Request", "isController": false}, {"data": [[11.590448013524904, 585.307480980558]], "isOverall": false, "label": "HTTP Request-Aggregated", "isController": false}], "supportsControllersDiscrimination": true, "maxX": 12.0, "title": "Time VS Threads"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    axisLabel: "Number of active threads",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average response times in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: { noColumns: 2,show: true, container: '#legendTimeVsThreads' },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s: At %x.2 active threads, Average response time was %y.2 ms"
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesTimeVsThreads"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotTimesVsThreads"), dataset, options);
            // setup overview
            $.plot($("#overviewTimesVsThreads"), dataset, prepareOverviewOptions(options));
        }
};

// Time vs threads
function refreshTimeVsThreads(){
    var infos = timeVsThreadsInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyTimeVsThreads");
        return;
    }
    if(isGraph($("#flotTimesVsThreads"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesTimeVsThreads");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotTimesVsThreads", "#overviewTimesVsThreads");
        $('#footerTimeVsThreads .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var bytesThroughputOverTimeInfos = {
        data : {"result": {"minY": 16.6, "minX": 1.77910338E12, "maxY": 1848.0, "series": [{"data": [[1.77910356E12, 1848.0], [1.77910374E12, 1848.0], [1.77910368E12, 1848.0], [1.77910338E12, 23.1], [1.77910386E12, 1848.0], [1.7791038E12, 1848.0], [1.7791035E12, 1848.0], [1.77910398E12, 1740.2], [1.77910344E12, 1670.9], [1.77910392E12, 1848.0], [1.77910362E12, 1848.0]], "isOverall": false, "label": "Bytes received per second", "isController": false}, {"data": [[1.77910356E12, 1328.0], [1.77910374E12, 1328.0], [1.77910368E12, 1328.0], [1.77910338E12, 16.6], [1.77910386E12, 1328.0], [1.7791038E12, 1328.0], [1.7791035E12, 1328.0], [1.77910398E12, 1250.5333333333333], [1.77910344E12, 1200.7333333333333], [1.77910392E12, 1328.0], [1.77910362E12, 1328.0]], "isOverall": false, "label": "Bytes sent per second", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77910398E12, "title": "Bytes Throughput Over Time"}},
        getOptions : function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity) ,
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Bytes / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendBytesThroughputOverTime'
                },
                selection: {
                    mode: "xy"
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y"
                }
            };
        },
        createGraph : function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesBytesThroughputOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotBytesThroughputOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewBytesThroughputOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Bytes throughput Over Time
function refreshBytesThroughputOverTime(fixTimestamps) {
    var infos = bytesThroughputOverTimeInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotBytesThroughputOverTime"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesBytesThroughputOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotBytesThroughputOverTime", "#overviewBytesThroughputOverTime");
        $('#footerBytesThroughputOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
}

var responseTimesOverTimeInfos = {
        data: {"result": {"minY": 579.5, "minX": 1.77910338E12, "maxY": 593.6958333333333, "series": [{"data": [[1.77910356E12, 582.0541666666663], [1.77910374E12, 583.4083333333336], [1.77910368E12, 584.71875], [1.77910338E12, 579.5], [1.77910386E12, 582.8479166666664], [1.7791038E12, 584.6187500000001], [1.7791035E12, 593.6958333333333], [1.77910398E12, 587.150442477876], [1.77910344E12, 587.2419354838705], [1.77910392E12, 583.9374999999992], [1.77910362E12, 583.7666666666673]], "isOverall": false, "label": "HTTP Request", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77910398E12, "title": "Response Time Over Time"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average response time in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimesOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Average response time was %y ms"
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesResponseTimesOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimesOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewResponseTimesOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Response Times Over Time
function refreshResponseTimeOverTime(fixTimestamps) {
    var infos = responseTimesOverTimeInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyResponseTimeOverTime");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotResponseTimesOverTime"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesResponseTimesOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimesOverTime", "#overviewResponseTimesOverTime");
        $('#footerResponseTimesOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var latenciesOverTimeInfos = {
        data: {"result": {"minY": 579.0, "minX": 1.77910338E12, "maxY": 593.6645833333332, "series": [{"data": [[1.77910356E12, 582.0333333333338], [1.77910374E12, 583.3958333333339], [1.77910368E12, 584.6958333333329], [1.77910338E12, 579.0], [1.77910386E12, 582.8416666666673], [1.7791038E12, 584.6], [1.7791035E12, 593.6645833333332], [1.77910398E12, 587.1438053097346], [1.77910344E12, 587.2004608294943], [1.77910392E12, 583.9354166666657], [1.77910362E12, 583.7395833333333]], "isOverall": false, "label": "HTTP Request", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77910398E12, "title": "Latencies Over Time"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average response latencies in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendLatenciesOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Average latency was %y ms"
                }
            };
        },
        createGraph: function () {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesLatenciesOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotLatenciesOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewLatenciesOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Latencies Over Time
function refreshLatenciesOverTime(fixTimestamps) {
    var infos = latenciesOverTimeInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyLatenciesOverTime");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotLatenciesOverTime"))) {
        infos.createGraph();
    }else {
        var choiceContainer = $("#choicesLatenciesOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotLatenciesOverTime", "#overviewLatenciesOverTime");
        $('#footerLatenciesOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var connectTimeOverTimeInfos = {
        data: {"result": {"minY": 0.10619469026548671, "minX": 1.77910338E12, "maxY": 3.833333333333333, "series": [{"data": [[1.77910356E12, 0.18124999999999994], [1.77910374E12, 0.1083333333333333], [1.77910368E12, 0.15416666666666684], [1.77910338E12, 3.833333333333333], [1.77910386E12, 0.11666666666666668], [1.7791038E12, 0.12916666666666668], [1.7791035E12, 0.2479166666666667], [1.77910398E12, 0.10619469026548671], [1.77910344E12, 0.163594470046083], [1.77910392E12, 0.11249999999999995], [1.77910362E12, 0.12291666666666669]], "isOverall": false, "label": "HTTP Request", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77910398E12, "title": "Connect Time Over Time"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getConnectTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average Connect Time in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendConnectTimeOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Average connect time was %y ms"
                }
            };
        },
        createGraph: function () {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesConnectTimeOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotConnectTimeOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewConnectTimeOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Connect Time Over Time
function refreshConnectTimeOverTime(fixTimestamps) {
    var infos = connectTimeOverTimeInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyConnectTimeOverTime");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotConnectTimeOverTime"))) {
        infos.createGraph();
    }else {
        var choiceContainer = $("#choicesConnectTimeOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotConnectTimeOverTime", "#overviewConnectTimeOverTime");
        $('#footerConnectTimeOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var responseTimePercentilesOverTimeInfos = {
        data: {"result": {"minY": 508.0, "minX": 1.77910338E12, "maxY": 1299.0, "series": [{"data": [[1.77910356E12, 677.0], [1.77910374E12, 841.0], [1.77910368E12, 835.0], [1.77910338E12, 623.0], [1.77910386E12, 678.0], [1.7791038E12, 791.0], [1.7791035E12, 1299.0], [1.77910398E12, 925.0], [1.77910344E12, 767.0], [1.77910392E12, 787.0], [1.77910362E12, 726.0]], "isOverall": false, "label": "Max", "isController": false}, {"data": [[1.77910356E12, 574.0], [1.77910374E12, 574.0], [1.77910368E12, 574.0], [1.77910338E12, 508.0], [1.77910386E12, 572.0], [1.7791038E12, 574.0], [1.7791035E12, 574.0], [1.77910398E12, 573.0], [1.77910344E12, 509.0], [1.77910392E12, 574.0], [1.77910362E12, 574.0]], "isOverall": false, "label": "Min", "isController": false}, {"data": [[1.77910356E12, 586.0], [1.77910374E12, 586.0], [1.77910368E12, 585.9000000000001], [1.77910338E12, 623.0], [1.77910386E12, 585.0], [1.7791038E12, 585.9000000000001], [1.7791035E12, 605.9000000000001], [1.77910398E12, 585.7], [1.77910344E12, 616.5], [1.77910392E12, 585.0], [1.77910362E12, 586.0]], "isOverall": false, "label": "90th percentile", "isController": false}, {"data": [[1.77910356E12, 661.04], [1.77910374E12, 672.19], [1.77910368E12, 707.4799999999998], [1.77910338E12, 623.0], [1.77910386E12, 672.19], [1.7791038E12, 682.1299999999999], [1.7791035E12, 1188.84], [1.77910398E12, 793.7999999999959], [1.77910344E12, 766.0], [1.77910392E12, 684.1699999999998], [1.77910362E12, 674.0]], "isOverall": false, "label": "99th percentile", "isController": false}, {"data": [[1.77910356E12, 579.0], [1.77910374E12, 579.0], [1.77910368E12, 578.0], [1.77910338E12, 609.0], [1.77910386E12, 578.0], [1.7791038E12, 578.0], [1.7791035E12, 580.0], [1.77910398E12, 579.0], [1.77910344E12, 580.0], [1.77910392E12, 578.0], [1.77910362E12, 579.0]], "isOverall": false, "label": "Median", "isController": false}, {"data": [[1.77910356E12, 593.95], [1.77910374E12, 608.0], [1.77910368E12, 621.9], [1.77910338E12, 623.0], [1.77910386E12, 610.95], [1.7791038E12, 625.8499999999999], [1.7791035E12, 649.8499999999999], [1.77910398E12, 643.7499999999998], [1.77910344E12, 651.0], [1.77910392E12, 620.6499999999999], [1.77910362E12, 613.95]], "isOverall": false, "label": "95th percentile", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77910398E12, "title": "Response Time Percentiles Over Time (successful requests only)"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true,
                        fill: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Response Time in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimePercentilesOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Response time was %y ms"
                }
            };
        },
        createGraph: function () {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesResponseTimePercentilesOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimePercentilesOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewResponseTimePercentilesOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Response Time Percentiles Over Time
function refreshResponseTimePercentilesOverTime(fixTimestamps) {
    var infos = responseTimePercentilesOverTimeInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotResponseTimePercentilesOverTime"))) {
        infos.createGraph();
    }else {
        var choiceContainer = $("#choicesResponseTimePercentilesOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimePercentilesOverTime", "#overviewResponseTimePercentilesOverTime");
        $('#footerResponseTimePercentilesOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};


var responseTimeVsRequestInfos = {
    data: {"result": {"minY": 558.5, "minX": 1.0, "maxY": 720.0, "series": [{"data": [[1.0, 618.5], [2.0, 558.5], [4.0, 577.0], [8.0, 579.0], [9.0, 580.0], [5.0, 578.0], [10.0, 580.0], [11.0, 584.0], [6.0, 579.0], [3.0, 619.0], [12.0, 720.0], [7.0, 580.0]], "isOverall": false, "label": "Successes", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 1000, "maxX": 12.0, "title": "Response Time Vs Request"}},
    getOptions: function() {
        return {
            series: {
                lines: {
                    show: false
                },
                points: {
                    show: true
                }
            },
            xaxis: {
                axisLabel: "Global number of requests per second",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            yaxis: {
                axisLabel: "Median Response Time in ms",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            legend: {
                noColumns: 2,
                show: true,
                container: '#legendResponseTimeVsRequest'
            },
            selection: {
                mode: 'xy'
            },
            grid: {
                hoverable: true // IMPORTANT! this is needed for tooltip to work
            },
            tooltip: true,
            tooltipOpts: {
                content: "%s : Median response time at %x req/s was %y ms"
            },
            colors: ["#9ACD32", "#FF6347"]
        };
    },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesResponseTimeVsRequest"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotResponseTimeVsRequest"), dataset, options);
        // setup overview
        $.plot($("#overviewResponseTimeVsRequest"), dataset, prepareOverviewOptions(options));

    }
};

// Response Time vs Request
function refreshResponseTimeVsRequest() {
    var infos = responseTimeVsRequestInfos;
    prepareSeries(infos.data);
    if (isGraph($("#flotResponseTimeVsRequest"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesResponseTimeVsRequest");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimeVsRequest", "#overviewResponseTimeVsRequest");
        $('#footerResponseRimeVsRequest .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};


var latenciesVsRequestInfos = {
    data: {"result": {"minY": 558.5, "minX": 1.0, "maxY": 720.0, "series": [{"data": [[1.0, 617.0], [2.0, 558.5], [4.0, 577.0], [8.0, 579.0], [9.0, 580.0], [5.0, 578.0], [10.0, 580.0], [11.0, 584.0], [6.0, 579.0], [3.0, 619.0], [12.0, 720.0], [7.0, 580.0]], "isOverall": false, "label": "Successes", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 1000, "maxX": 12.0, "title": "Latencies Vs Request"}},
    getOptions: function() {
        return{
            series: {
                lines: {
                    show: false
                },
                points: {
                    show: true
                }
            },
            xaxis: {
                axisLabel: "Global number of requests per second",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            yaxis: {
                axisLabel: "Median Latency in ms",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            legend: { noColumns: 2,show: true, container: '#legendLatencyVsRequest' },
            selection: {
                mode: 'xy'
            },
            grid: {
                hoverable: true // IMPORTANT! this is needed for tooltip to work
            },
            tooltip: true,
            tooltipOpts: {
                content: "%s : Median Latency time at %x req/s was %y ms"
            },
            colors: ["#9ACD32", "#FF6347"]
        };
    },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesLatencyVsRequest"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotLatenciesVsRequest"), dataset, options);
        // setup overview
        $.plot($("#overviewLatenciesVsRequest"), dataset, prepareOverviewOptions(options));
    }
};

// Latencies vs Request
function refreshLatenciesVsRequest() {
        var infos = latenciesVsRequestInfos;
        prepareSeries(infos.data);
        if(isGraph($("#flotLatenciesVsRequest"))){
            infos.createGraph();
        }else{
            var choiceContainer = $("#choicesLatencyVsRequest");
            createLegend(choiceContainer, infos);
            infos.createGraph();
            setGraphZoomable("#flotLatenciesVsRequest", "#overviewLatenciesVsRequest");
            $('#footerLatenciesVsRequest .legendColorBox > div').each(function(i){
                $(this).clone().prependTo(choiceContainer.find("li").eq(i));
            });
        }
};

var hitsPerSecondInfos = {
        data: {"result": {"minY": 0.11666666666666667, "minX": 1.77910338E12, "maxY": 8.0, "series": [{"data": [[1.77910356E12, 8.0], [1.77910374E12, 8.0], [1.77910368E12, 8.0], [1.77910338E12, 0.11666666666666667], [1.77910386E12, 8.0], [1.7791038E12, 8.0], [1.7791035E12, 8.0], [1.77910398E12, 7.45], [1.77910344E12, 7.3], [1.77910392E12, 8.0], [1.77910362E12, 8.0]], "isOverall": false, "label": "hitsPerSecond", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77910398E12, "title": "Hits Per Second"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of hits / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendHitsPerSecond"
                },
                selection: {
                    mode : 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y.2 hits/sec"
                }
            };
        },
        createGraph: function createGraph() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesHitsPerSecond"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotHitsPerSecond"), dataset, options);
            // setup overview
            $.plot($("#overviewHitsPerSecond"), dataset, prepareOverviewOptions(options));
        }
};

// Hits per second
function refreshHitsPerSecond(fixTimestamps) {
    var infos = hitsPerSecondInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if (isGraph($("#flotHitsPerSecond"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesHitsPerSecond");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotHitsPerSecond", "#overviewHitsPerSecond");
        $('#footerHitsPerSecond .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
}

var codesPerSecondInfos = {
        data: {"result": {"minY": 0.1, "minX": 1.77910338E12, "maxY": 8.0, "series": [{"data": [[1.77910356E12, 8.0], [1.77910374E12, 8.0], [1.77910368E12, 8.0], [1.77910338E12, 0.1], [1.77910386E12, 8.0], [1.7791038E12, 8.0], [1.7791035E12, 8.0], [1.77910398E12, 7.533333333333333], [1.77910344E12, 7.233333333333333], [1.77910392E12, 8.0], [1.77910362E12, 8.0]], "isOverall": false, "label": "200", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77910398E12, "title": "Codes Per Second"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of responses / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendCodesPerSecond"
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "Number of Response Codes %s at %x was %y.2 responses / sec"
                }
            };
        },
    createGraph: function() {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesCodesPerSecond"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotCodesPerSecond"), dataset, options);
        // setup overview
        $.plot($("#overviewCodesPerSecond"), dataset, prepareOverviewOptions(options));
    }
};

// Codes per second
function refreshCodesPerSecond(fixTimestamps) {
    var infos = codesPerSecondInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotCodesPerSecond"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesCodesPerSecond");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotCodesPerSecond", "#overviewCodesPerSecond");
        $('#footerCodesPerSecond .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var transactionsPerSecondInfos = {
        data: {"result": {"minY": 0.1, "minX": 1.77910338E12, "maxY": 8.0, "series": [{"data": [[1.77910356E12, 8.0], [1.77910374E12, 8.0], [1.77910368E12, 8.0], [1.77910338E12, 0.1], [1.77910386E12, 8.0], [1.7791038E12, 8.0], [1.7791035E12, 8.0], [1.77910398E12, 7.533333333333333], [1.77910344E12, 7.233333333333333], [1.77910392E12, 8.0], [1.77910362E12, 8.0]], "isOverall": false, "label": "HTTP Request-success", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77910398E12, "title": "Transactions Per Second"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of transactions / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendTransactionsPerSecond"
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y transactions / sec"
                }
            };
        },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesTransactionsPerSecond"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotTransactionsPerSecond"), dataset, options);
        // setup overview
        $.plot($("#overviewTransactionsPerSecond"), dataset, prepareOverviewOptions(options));
    }
};

// Transactions per second
function refreshTransactionsPerSecond(fixTimestamps) {
    var infos = transactionsPerSecondInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyTransactionsPerSecond");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotTransactionsPerSecond"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesTransactionsPerSecond");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotTransactionsPerSecond", "#overviewTransactionsPerSecond");
        $('#footerTransactionsPerSecond .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var totalTPSInfos = {
        data: {"result": {"minY": 0.1, "minX": 1.77910338E12, "maxY": 8.0, "series": [{"data": [[1.77910356E12, 8.0], [1.77910374E12, 8.0], [1.77910368E12, 8.0], [1.77910338E12, 0.1], [1.77910386E12, 8.0], [1.7791038E12, 8.0], [1.7791035E12, 8.0], [1.77910398E12, 7.533333333333333], [1.77910344E12, 7.233333333333333], [1.77910392E12, 8.0], [1.77910362E12, 8.0]], "isOverall": false, "label": "Transaction-success", "isController": false}, {"data": [], "isOverall": false, "label": "Transaction-failure", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77910398E12, "title": "Total Transactions Per Second"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of transactions / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendTotalTPS"
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y transactions / sec"
                },
                colors: ["#9ACD32", "#FF6347"]
            };
        },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesTotalTPS"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotTotalTPS"), dataset, options);
        // setup overview
        $.plot($("#overviewTotalTPS"), dataset, prepareOverviewOptions(options));
    }
};

// Total Transactions per second
function refreshTotalTPS(fixTimestamps) {
    var infos = totalTPSInfos;
    // We want to ignore seriesFilter
    prepareSeries(infos.data, false, true);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotTotalTPS"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesTotalTPS");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotTotalTPS", "#overviewTotalTPS");
        $('#footerTotalTPS .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

// Collapse the graph matching the specified DOM element depending the collapsed
// status
function collapse(elem, collapsed){
    if(collapsed){
        $(elem).parent().find(".fa-chevron-up").removeClass("fa-chevron-up").addClass("fa-chevron-down");
    } else {
        $(elem).parent().find(".fa-chevron-down").removeClass("fa-chevron-down").addClass("fa-chevron-up");
        if (elem.id == "bodyBytesThroughputOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshBytesThroughputOverTime(true);
            }
            document.location.href="#bytesThroughputOverTime";
        } else if (elem.id == "bodyLatenciesOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshLatenciesOverTime(true);
            }
            document.location.href="#latenciesOverTime";
        } else if (elem.id == "bodyCustomGraph") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshCustomGraph(true);
            }
            document.location.href="#responseCustomGraph";
        } else if (elem.id == "bodyConnectTimeOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshConnectTimeOverTime(true);
            }
            document.location.href="#connectTimeOverTime";
        } else if (elem.id == "bodyResponseTimePercentilesOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshResponseTimePercentilesOverTime(true);
            }
            document.location.href="#responseTimePercentilesOverTime";
        } else if (elem.id == "bodyResponseTimeDistribution") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshResponseTimeDistribution();
            }
            document.location.href="#responseTimeDistribution" ;
        } else if (elem.id == "bodySyntheticResponseTimeDistribution") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshSyntheticResponseTimeDistribution();
            }
            document.location.href="#syntheticResponseTimeDistribution" ;
        } else if (elem.id == "bodyActiveThreadsOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshActiveThreadsOverTime(true);
            }
            document.location.href="#activeThreadsOverTime";
        } else if (elem.id == "bodyTimeVsThreads") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshTimeVsThreads();
            }
            document.location.href="#timeVsThreads" ;
        } else if (elem.id == "bodyCodesPerSecond") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshCodesPerSecond(true);
            }
            document.location.href="#codesPerSecond";
        } else if (elem.id == "bodyTransactionsPerSecond") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshTransactionsPerSecond(true);
            }
            document.location.href="#transactionsPerSecond";
        } else if (elem.id == "bodyTotalTPS") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshTotalTPS(true);
            }
            document.location.href="#totalTPS";
        } else if (elem.id == "bodyResponseTimeVsRequest") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshResponseTimeVsRequest();
            }
            document.location.href="#responseTimeVsRequest";
        } else if (elem.id == "bodyLatenciesVsRequest") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshLatenciesVsRequest();
            }
            document.location.href="#latencyVsRequest";
        }
    }
}

/*
 * Activates or deactivates all series of the specified graph (represented by id parameter)
 * depending on checked argument.
 */
function toggleAll(id, checked){
    var placeholder = document.getElementById(id);

    var cases = $(placeholder).find(':checkbox');
    cases.prop('checked', checked);
    $(cases).parent().children().children().toggleClass("legend-disabled", !checked);

    var choiceContainer;
    if ( id == "choicesBytesThroughputOverTime"){
        choiceContainer = $("#choicesBytesThroughputOverTime");
        refreshBytesThroughputOverTime(false);
    } else if(id == "choicesResponseTimesOverTime"){
        choiceContainer = $("#choicesResponseTimesOverTime");
        refreshResponseTimeOverTime(false);
    }else if(id == "choicesResponseCustomGraph"){
        choiceContainer = $("#choicesResponseCustomGraph");
        refreshCustomGraph(false);
    } else if ( id == "choicesLatenciesOverTime"){
        choiceContainer = $("#choicesLatenciesOverTime");
        refreshLatenciesOverTime(false);
    } else if ( id == "choicesConnectTimeOverTime"){
        choiceContainer = $("#choicesConnectTimeOverTime");
        refreshConnectTimeOverTime(false);
    } else if ( id == "choicesResponseTimePercentilesOverTime"){
        choiceContainer = $("#choicesResponseTimePercentilesOverTime");
        refreshResponseTimePercentilesOverTime(false);
    } else if ( id == "choicesResponseTimePercentiles"){
        choiceContainer = $("#choicesResponseTimePercentiles");
        refreshResponseTimePercentiles();
    } else if(id == "choicesActiveThreadsOverTime"){
        choiceContainer = $("#choicesActiveThreadsOverTime");
        refreshActiveThreadsOverTime(false);
    } else if ( id == "choicesTimeVsThreads"){
        choiceContainer = $("#choicesTimeVsThreads");
        refreshTimeVsThreads();
    } else if ( id == "choicesSyntheticResponseTimeDistribution"){
        choiceContainer = $("#choicesSyntheticResponseTimeDistribution");
        refreshSyntheticResponseTimeDistribution();
    } else if ( id == "choicesResponseTimeDistribution"){
        choiceContainer = $("#choicesResponseTimeDistribution");
        refreshResponseTimeDistribution();
    } else if ( id == "choicesHitsPerSecond"){
        choiceContainer = $("#choicesHitsPerSecond");
        refreshHitsPerSecond(false);
    } else if(id == "choicesCodesPerSecond"){
        choiceContainer = $("#choicesCodesPerSecond");
        refreshCodesPerSecond(false);
    } else if ( id == "choicesTransactionsPerSecond"){
        choiceContainer = $("#choicesTransactionsPerSecond");
        refreshTransactionsPerSecond(false);
    } else if ( id == "choicesTotalTPS"){
        choiceContainer = $("#choicesTotalTPS");
        refreshTotalTPS(false);
    } else if ( id == "choicesResponseTimeVsRequest"){
        choiceContainer = $("#choicesResponseTimeVsRequest");
        refreshResponseTimeVsRequest();
    } else if ( id == "choicesLatencyVsRequest"){
        choiceContainer = $("#choicesLatencyVsRequest");
        refreshLatenciesVsRequest();
    }
    var color = checked ? "black" : "#818181";
    if(choiceContainer != null) {
        choiceContainer.find("label").each(function(){
            this.style.color = color;
        });
    }
}

