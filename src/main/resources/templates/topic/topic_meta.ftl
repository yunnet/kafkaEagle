<!DOCTYPE html>
<html lang="zh">

<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">

<title>Topic Meta - KafkaEagle</title>
<#include "../public/css.ftl" encoding="UTF-8">
<#include "../public/tcss.ftl" encoding="UTF-8">
</head>

<body>
	<#include "../public/navbar.ftl" encoding="UTF-8"> 
	<div id="wrapper">
		<div id="page-wrapper">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">
						Topic <small>meta</small>
					</h1>
				</div>
				<!-- /.col-lg-12 -->
			</div>
			
			<!-- /.row -->
			<div class="row">
				<div class="col-lg-12">
					<div class="alert alert-info alert-dismissable">
						<button type="button" class="close" data-dismiss="alert"
							aria-hidden="true">×</button>
						<i class="fa fa-info-circle"></i> <strong>List all topic
							meta information. Here -1 indicates that the result is not
							available or is empty.</strong>
					</div>
				</div>
			</div>			
			<!-- /.row -->
			
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-heading">
							<i class="fa fa-tasks fa-fw"></i> Topic Meta Info
							<div class="pull-right"></div>
						</div>
						
						<!-- /.panel-heading -->
						<div class="panel-body">
							<table id="result" class="table table-bordered table-condensed" width="100%">
								<thead>
									<tr>
										<th>Topic</th>
										<th>Partition</th>
										<th>Leader</th>
										<th>Replicas</th>
										<th>Isr</th>
									</tr>
								</thead>
							</table>
						</div>
						<!-- /.panel-body -->
					</div>
				</div>
				<!-- /.col-lg-4 -->
			</div>
			<!-- /.row -->
		</div>
		<!-- /#page-wrapper -->
	</div>
</body>

<#include "../public/script.ftl">
<script src="${springMacroRequestContext.contextPath}/static/js/main/topic/topic.meta.js" type="text/javascript"></script>
<#include "../public/tscript.ftl"> 
</html>