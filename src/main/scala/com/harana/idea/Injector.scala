package com.harana.idea

import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef._
import org.jetbrains.plugins.scala.lang.psi.impl.toplevel.typedef.SyntheticMembersInjector

class Injector extends SyntheticMembersInjector {

	override def injectFunctions(source: ScTypeDefinition): Seq[String] = {

		if (source == null || source.getAnnotations == null) Seq()

		source.getAnnotations
			.filter { a => a != null && a.getQualifiedName.startsWith("com.harana.annotations.Repository") }
			.map { a =>
				val text = a.getParameterList.getChildren.head.getText
				val clsTypeName = text.substring(text.indexOf("(")+1, text.length - 1)
				val clsType = s"com.harana.sdk.models.$clsTypeName"
				val clsId = s"$clsType.${clsTypeName}Id"

				Seq(s"def create(item: $clsType): scala.concurrent.Future[$clsType] = ???",
						s"def create(items: List[$clsType]): scala.concurrent.Future[List[$clsType]] = ???",
						s"def get(id: $clsId): scala.concurrent.Future[Option[$clsType]] = ???",
						s"def get(ids: List[$clsId]): scala.concurrent.Future[List[$clsType]] = ???",
						s"def get(attributes: Map[String, String]): scala.concurrent.Future[Option[$clsType]] = ???",
						s"def update(item: $clsType): scala.concurrent.Future[Unit] = ???",
						s"def update(items: List[$clsType]): scala.concurrent.Future[Unit] = ???",
						s"def delete(id: $clsId): scala.concurrent.Future[Unit] = ???",
						s"def delete(ids: List[$clsId]): scala.concurrent.Future[Unit] = ???"
				)
			}.headOption.getOrElse(Seq())
	}
}