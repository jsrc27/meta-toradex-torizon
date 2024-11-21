SUMMARY = "Generic Health Check Framework for systemd"
DESCRIPTION = "greenboot is a framework for systemd that helps to check \
system health at boot time, especially useful to identify if an update \
is successful or not, and rollback to a previous version if necessary."
SECTION = "base"
HOMEPAGE = "https://github.com/fedora-iot/greenboot"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1803fa9c2c3ce8cb06b4861d75310742"

SRC_URI = "\
    git://github.com/fedora-iot/greenboot.git;branch=main;protocol=https \
    file://greenboot.target \
    file://greenboot-status \
    file://redboot-auto-reboot \
    file://greenboot-logs \
    file://00_cleanup_uboot_vars.sh \
    file://01_log_rollback_info.sh \
    "

S = "${WORKDIR}/git"

SRCREV = "9f997198416415a426d0ac394e38a391dd9b2e0a"
PV = "0.15.7+git${SRCPV}"

RDEPENDS:${PN} = "bash"

inherit systemd

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "\
    greenboot-healthcheck.service \
    greenboot-status.service \
    greenboot-task-runner.service \
    redboot-task-runner.service \
    redboot-auto-reboot.service \
    redboot.target \
    greenboot.target \
    "

do_install() {
    # systemd units
    install -d ${D}${systemd_system_unitdir}
    install -m 644 ${S}/usr/lib/systemd/system/greenboot-healthcheck.service ${D}/${systemd_system_unitdir}
    install -m 644 ${S}/usr/lib/systemd/system/greenboot-status.service ${D}/${systemd_system_unitdir}
    install -m 644 ${S}/usr/lib/systemd/system/greenboot-task-runner.service ${D}/${systemd_system_unitdir}
    install -m 644 ${S}/usr/lib/systemd/system/redboot-task-runner.service ${D}/${systemd_system_unitdir}
    install -m 644 ${S}/usr/lib/systemd/system/redboot-auto-reboot.service ${D}/${systemd_system_unitdir}
    install -m 644 ${S}/usr/lib/systemd/system/redboot.target ${D}/${systemd_system_unitdir}
    install -m 644 ${UNPACKDIR}/greenboot.target ${D}/${systemd_system_unitdir}

    # helper scripts
    install -d ${D}${libexecdir}/greenboot
    install -m 755 ${S}/usr/libexec/greenboot/greenboot ${D}${libexecdir}/greenboot
    install -m 755 ${UNPACKDIR}/greenboot-status ${D}${libexecdir}/greenboot
    install -m 755 ${UNPACKDIR}/greenboot-logs ${D}${libexecdir}/greenboot
    install -m 755 ${UNPACKDIR}/redboot-auto-reboot ${D}${libexecdir}/greenboot

    # required scripts for success boot
    install -d ${D}${sysconfdir}/greenboot/check/required.d

    # wanted scripts for success boot
    install -d ${D}${sysconfdir}/greenboot/check/wanted.d

    # scripts to run in GREEN boot state
    install -d ${D}${sysconfdir}/greenboot/green.d
    install -m 755 ${UNPACKDIR}/00_cleanup_uboot_vars.sh ${D}${sysconfdir}/greenboot/green.d
    install -m 755 ${UNPACKDIR}/01_log_rollback_info.sh ${D}${sysconfdir}/greenboot/green.d

    # scripts to run in RED boot state
    install -d ${D}${sysconfdir}/greenboot/red.d
}
