<!DOCTYPE HTML>
<!--
    Future Imperfect by HTML5 UP
    html5up.net | @ajlkn
    Free for personal and commercial use under the CCA 3.0 license (html5up.net/license)
  -->
<html>

<%@ include file="head.jspf" %>

<body>

<!-- Wrapper -->
<div id="wrapper">

<%@ include file="header.jspf" %>
<!-- Main -->
<div id="main">
  <!-- Post -->
  <article class="post">
    <header>
      <div class="title">
        <h2>About</h2>
      </div>
    </header>
    
    <p>The Linutronix Test Environment (CI) automatically tests changes of the
    PREEMPT_RT patch on different hardware platforms with a defined test
    procedure. Following is a description of the defined test procedure and
    the hardware infrastructure.</p>

    <h3>Test Procedure</h3>

    <p>The test environment monitors the branches of the two git repositories
    hosting the PREEMPT_RT patches with the corresponding Linux mainline
    kernel. A new commit in a single branch triggers a test run for this
    branch. In a single test run, a defined set of kernel configurations are
    tested. The test procedure for a kernel configuration goes through up to
    three phases, depending on the specification:</p>

    <ol>
    
      <li> Build test: The kernel configuration (with a defined overlay, such
      as debug overlay) is compiled on a build server with the source of the
      updated branch. There is no need for an assigned target for every kernel
      configuration of this phase.</li>
      
      <li> Boot test: The built kernel and device tree are booted on the
      specified target. For this, kexec is used. This test is successful only
      if there is no warning output during boot.</li>
      
      <li> Cyclictest: After a successful boot, the latency measurement tool
      cyclictest is used to detect system latency under a defined load. If the
      latency exceeds a defined threshold, cyclictest aborts and this test
      phase fails. </li>
    </ol>

    <p>The state of the entire test run is a combination of all interim test
    phase results.</p>

    <h3>Infrastructure</h3>

    <p>
    <a href="images/ci-rt_infrastructure-setup.png" target=
    "_blank"><img src="images/ci-rt_infrastructure-setup.png" style="float:
    right;" width="420"/></a> The figure shows the structure of the CI-RT
    infrastructure. There is a single control instance, the Jenkins master,
    monitoring the branches of the PREEMPT_RT git repository. This instance
    starts the three different test phases on the Jenkins slaves. The first
    test phase is processed on the Jenkins slave "build server". The second
    and third test phases are processed on the test targets, which are also
    registered Jenkins slaves. The test targets are located in a test
    rack. This rack is similar to those in the well
    known <a href="https://www.osadl.org/Test-Rack.test-rack.0.html" target=
    "_blank">OSADL QA farm</a>. For abstract communication with the targets
    a tool called r4d (Remote For Dut) is used. In order to use a
    generic API, libvirt was adapted to communicate with r4d. <br> The test
    results are stored in the database of the web interface. The web interface
    displays the test results. <br> For setting up the infrastructure, please
    visit <a href="https://github.com/ci-rt" target= "_blank">CI-RT github
    project</a>.</p>

    <p>
    The standardized elbe based root file systems for the test targets can be
    downloaded
    <a href="https://ci-rt.linutronix.de/download/target-elbe-rfs/">here</a>.
    The test targets need to fulfil several requirements to be placed into the
    test rack
    (see <a href="https://ci-rt.linutronix.de/download/system-requirements.pdf">"System
    Requirements"</a> for more details). </p>

  </article>
</div>

<%@ include file="sidebar.jspf" %>
</div>

<!-- Scripts -->
<%@ include file="scripts.jspf" %>
<script src="assets/js/overview.js"></script>
<script>embedCharts()</script>

</body>
</html>
