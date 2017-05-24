<!DOCTYPE html>
<html lang="zh">

<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">

<title>Zookeeper Cli - KafkaEagle</title>
<#include "../public/css.ftl" encoding="UTF-8">
</head>

<body>
	<#include "../public/navbar.ftl" encoding="UTF-8"> 
	<div id="wrapper">
		<div id="page-wrapper">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">
						Zookeeper Client <small>overview</small>
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
						<i class="fa fa-info-circle"></i> <strong>Use the zookeeper client command to operate the zookeeper cluster.</strong> If you
						don't know the usage of Zookeeper, you can visit the
						website of <a
							href="http://zookeeper.apache.org/" target="_blank"
							class="alert-link">Zookeeper</a> to view the relevant usage.
					</div>
				</div>
			</div>
			<!-- /.row -->
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-heading">
							<i class="fa fa-code fa-fw"></i> Zookeeper Client Info
							<div class="pull-right"></div>
						</div>
						<!-- /.panel-heading -->
						<div class="panel-body">
							<div id="zkcli_info" class="terminal" style="height: 400px;">
								
							</div>
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
<script src="${springMacroRequestContext.contextPath}/static/js/plugins/terminal/jquery.terminal.min.js" type="text/javascript"></script>
<script src="${springMacroRequestContext.contextPath}/static/js/main/cluster/zkcli.js" type="text/javascript"></script>

</html>