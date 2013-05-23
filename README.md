Multi-Projector-Mapper (MPM)
============================

Java / OpenGL based framework for multi-projector 3D mapping and more.

Overview
--------

The multi-projector-mapper (MPM) is a software framework for 3D projection mapping using multiple projector. It contains a basic rendering infrastructure, and interactive tools for projector calibration. For calibration, the method given in Oliver Bimber and Ramesh Raskar's book "Spatial Augmented Reality" (http://www.amazon.com/Spatial-Augmented-Reality-Merging-Virtual/dp/1568812302), Appendix A, is used.

The framework is the outcome of the "Projections of Reality" cluster at smartgeometry 2013 (http://www.smartgeometry.org/), and is to be seen as a prototype that can be used for developing specialized projection mapping applications. The projector calibration method alone could also be used to output the OpenGL projection and modelview matrices, that then can be used by other applications. Also, the more generic code might serve as well as a starting point for those who want to dive into pure Java / OpenGL coding (e.g. when coming from Processing).

Currently, at ETH Zurich's Future Cities Laboratory (http://www.futurecities.ethz.ch) we continue to work on the code. Upcoming features will be integration of the 3D scene analysis component, that was so far realized by a separate application.

Repository
----------

The repository contains an Eclipse project, including dependencies such as JOGL etc. Thus the code should run out of the box on Mac OS X, Windows and Linux.


Further Info & Contact
----------------------

A general overview of the "Projections of Reality" work is at http://www.arisona.ch/web/projections-of-reality/

Also, there's a short video documentation at https://vimeo.com/65130490

For questions etc. feel free to be in touch with me (Stefan Müller Arisona) at arisona@arch.ethz.ch

