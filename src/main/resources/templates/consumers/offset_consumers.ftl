<!DOCTYPE html>
<html lang="zh">

<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">

<title>Offsets - KafkaEagle</title>
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
						Consumers Offsets <small>details</small>
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
						<i class="fa fa-info-circle"></i> <strong>List the
							current consumers's offsets of topic.</strong>
					</div>
				</div>
			</div>
			<!-- /.row -->
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div id="topic_name_header" class="panel-heading">
							<i class="fa fa-user fa-fw"></i> <strong>{TopicName}</strong>
							<div class="pull-right"></div>
						</div>
						<!-- /.panel-heading -->
						<div id="offset_topic_info" class="panel-body"></div>
						<!-- /.panel-body -->
					</div>
				</div>
				<!-- /.col-lg-4 -->
			</div>
		</div>
		<!-- /#page-wrapper -->
	</div>
</body>

<#include "../public/script.ftl">
<script src="${springMacroRequestContext.contextPath}/static/js/main/consumer/offset.consumer.js" type="text/javascript"></script>
<#include "../public/tscript.ftl"> 
</html>
